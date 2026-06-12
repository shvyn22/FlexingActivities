package shvyn22.flexingactivities.domain.weather.resolvers.scoring

import shvyn22.flexingactivities.domain.weather.model.HourlyWeather

private val DAYTIME_HOURS = 6..20

/**
 * Groups hourly weather points by calendar date and aggregates them into [DailyWeather] entries.
 * Averages (temperature, humidity, wind, etc.) are computed over daytime hours (6–20) only;
 * precipitation totals and snow depth use all 24 hours so nothing is missed overnight.
 */
internal fun HourlyWeather.toDailyWeatherList(): List<DailyWeather> {
    return points
        .groupBy { it.time.toLocalDate() }
        .entries
        .sortedBy { it.key }
        .map { (date, points) ->
            val daytimePoints = points.filter { it.time.hour in DAYTIME_HOURS }.ifEmpty { points }

            DailyWeather(
                date = date,
                temperature = daytimePoints.map { it.temperature }.average(),
                apparentTemperature = daytimePoints.map { it.apparentTemperature }.average(),
                relativeHumidity = daytimePoints.map { it.relativeHumidity }.average(),
                precipitationProbability = daytimePoints.map { it.precipitationProbability }
                    .average(),
                pressureMsl = daytimePoints.map { it.pressureMsl }.average(),
                cloudCover = daytimePoints.map { it.cloudCover }.average(),
                visibility = daytimePoints.map { it.visibility }.average(),
                windSpeed10m = daytimePoints.map { it.windSpeed10m }.average(),
                windGusts10m = daytimePoints.map { it.windGusts10m }.average(),
                temperatureAllDay = points.map { it.temperature }.average(),
                windSpeed10mAllDay = points.map { it.windSpeed10m }.average(),
                precipitation = points.sumOf { it.precipitation },
                rain = points.sumOf { it.rain },
                showers = points.sumOf { it.showers },
                snowfall = points.sumOf { it.snowfall },
                snowDepth = points.maxOf { it.snowDepth },
                weatherCode = points.maxOf { it.weatherCode },
            )
        }
}

/**
 * Piecewise-linear score (0–100).
 * Inside [idealLow, idealHigh] → 100.
 * At/beyond hardLow or hardHigh → 0.
 * Null hard boundary → tapers symmetrically to 0 at a distance equal to the ideal range width.
 */
internal fun score(
    value: Double,
    idealLow: Double,
    idealHigh: Double,
    hardLow: Double? = null,
    hardHigh: Double? = null,
): Int {
    if (value in idealLow..idealHigh) return 100

    val rangeWidth = idealHigh - idealLow

    return when {
        value < idealLow -> {
            val floor = hardLow ?: (idealLow - rangeWidth)
            if (value <= floor) return 0
            ((value - floor) / (idealLow - floor) * 100).toInt().coerceIn(0, 100)
        }

        else -> {
            val ceil = hardHigh ?: (idealHigh + rangeWidth)
            if (value >= ceil) return 0
            ((ceil - value) / (ceil - idealHigh) * 100).toInt().coerceIn(0, 100)
        }
    }
}

/** Cloud cover score with a soft floor of ~30 at extremes (no hard penalty). */
internal fun cloudCoverScore(
    cloudCover: Double,
    idealLow: Double,
    idealHigh: Double
): Int {
    if (cloudCover in idealLow..idealHigh) return 100

    return when {
        cloudCover < idealLow ->
            (30 + (cloudCover / idealLow.coerceAtLeast(1.0)) * 70)
                .toInt().coerceIn(0, 100)

        else ->
            (30 + ((100.0 - cloudCover) / (100.0 - idealHigh).coerceAtLeast(1.0)) * 70)
                .toInt().coerceIn(0, 100)
    }
}

/**
 * Temperature discomfort score for Indoor sightseeing.
 * Comfortable outdoors (18–25°C) → 0; freezing/heat → 100.
 */
internal fun temperatureDiscomfortScore(
    temp: Double
): Int = when {
    temp in 18.0..25.0 -> 0

    temp < 18.0 -> when {
        temp <= -10.0 -> 100
        temp < 0.0 -> (50 + (0.0 - temp) / 10.0 * 50).toInt().coerceIn(0, 100)
        else -> ((18.0 - temp) / 18.0 * 50).toInt().coerceIn(0, 100)
    }

    else -> when {
        temp >= 40.0 -> 100
        temp > 35.0 -> (50 + (temp - 35.0) / 5.0 * 50).toInt().coerceIn(0, 100)
        else -> ((temp - 25.0) / 10.0 * 50).toInt().coerceIn(0, 100)
    }
}

/**
 * Severe WMO weather codes that push Indoor sightseeing score up.
 *
 * Only codes that represent a qualitatively unique hazard not already captured by another
 * parameter are scored here:
 * - Codes 0–3 (clear/overcast): covered by the cloud cover parameter.
 * - Codes 51–55, 61–65, 80–82 (drizzle, rain, rain showers): covered by the precipitation parameter.
 *
 * Based on: https://open-meteo.com/en/docs (weathercode variable)
 */
internal fun weatherCodeSeverityScore(
    code: Int
): Int = when (code) {
    in 45..48 -> 70    // fog or depositing rime fog
    56, 57 -> 60            // freezing drizzle, light and heavy (icing hazard not captured by precipitation volume)
    66, 67 -> 70            // freezing rain, light and heavy (icing hazard not captured by precipitation volume)
    in 71..77 -> 80    // snowfall slight–heavy, snow grains
    85, 86 -> 80            // snow showers, slight and heavy
    in 95..99 -> 100   // thunderstorms (with or without hail)
    else -> 0
}
