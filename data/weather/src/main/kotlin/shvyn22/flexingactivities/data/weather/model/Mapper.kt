package shvyn22.flexingactivities.data.weather.model

import shvyn22.flexingactivities.domain.weather.model.HourlyPoint
import shvyn22.flexingactivities.domain.weather.model.HourlyWeather
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal fun ForecastResponseDto.toDomain(): HourlyWeather {
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    val points = hourly.time.indices.map { i ->
        HourlyPoint(
            time = LocalDateTime.parse(hourly.time[i], formatter),
            temperature = hourly.temperature2m[i],
            apparentTemperature = hourly.apparentTemperature[i],
            relativeHumidity = hourly.relativeHumidity2m[i],
            precipitationProbability = hourly.precipitationProbability.getOrElse(i) { 0.0 },
            precipitation = hourly.precipitation[i],
            rain = hourly.rain[i],
            showers = hourly.showers[i],
            snowfall = hourly.snowfall[i],
            snowDepth = hourly.snowDepth[i] * 100.0,
            pressureMsl = hourly.pressureMsl[i],
            cloudCover = hourly.cloudCover[i],
            visibility = hourly.visibility[i],
            windSpeed10m = hourly.windSpeed10m[i],
            windGusts10m = hourly.windGusts10m[i],
            windDirection10m = hourly.windDirection10m[i],
            weatherCode = hourly.weatherCode[i],
        )
    }
    return HourlyWeather(points, timezone, utcOffsetSeconds)
}