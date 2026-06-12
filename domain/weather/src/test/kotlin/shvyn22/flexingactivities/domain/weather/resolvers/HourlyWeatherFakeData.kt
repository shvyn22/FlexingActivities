package shvyn22.flexingactivities.domain.weather.resolvers

import shvyn22.flexingactivities.domain.weather.model.HourlyPoint
import shvyn22.flexingactivities.domain.weather.model.HourlyWeather
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

internal val TEST_DATE: LocalDate = LocalDate.of(2024, 1, 15)

internal fun buildHourlyWeather(
    vararg points: HourlyPoint,
    timezone: String = "UTC",
    utcOffsetSeconds: Int = 0,
): HourlyWeather {
    return HourlyWeather(
        points = points.toList(),
        timezone = timezone,
        utcOffsetSeconds = utcOffsetSeconds,
    )
}

internal fun buildPoint(
    date: LocalDate = TEST_DATE,
    hour: Int = 12,
    temperature: Double = 15.0,
    apparentTemperature: Double = 15.0,
    relativeHumidity: Double = 60.0,
    precipitationProbability: Double = 10.0,
    precipitation: Double = 0.0,
    rain: Double = 0.0,
    showers: Double = 0.0,
    snowfall: Double = 0.0,
    snowDepth: Double = 0.0,
    pressureMsl: Double = 1013.0,
    cloudCover: Double = 30.0,
    visibility: Double = 20000.0,
    windSpeed10m: Double = 3.0,
    windGusts10m: Double = 5.0,
    windDirection10m: Double = 180.0,
    weatherCode: Int = 0,
): HourlyPoint {
    return HourlyPoint(
        time = LocalDateTime.of(date, LocalTime.of(hour, 0)),
        temperature = temperature,
        apparentTemperature = apparentTemperature,
        relativeHumidity = relativeHumidity,
        precipitationProbability = precipitationProbability,
        precipitation = precipitation,
        rain = rain,
        showers = showers,
        snowfall = snowfall,
        snowDepth = snowDepth,
        pressureMsl = pressureMsl,
        cloudCover = cloudCover,
        visibility = visibility,
        windSpeed10m = windSpeed10m,
        windGusts10m = windGusts10m,
        windDirection10m = windDirection10m,
        weatherCode = weatherCode,
    )
}
