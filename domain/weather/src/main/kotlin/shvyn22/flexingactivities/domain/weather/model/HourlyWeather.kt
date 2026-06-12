package shvyn22.flexingactivities.domain.weather.model

import java.time.LocalDateTime

data class HourlyWeather(
    val points: List<HourlyPoint>,
    val timezone: String,
    val utcOffsetSeconds: Int,
)

data class HourlyPoint(
    val time: LocalDateTime,
    val temperature: Double,
    val apparentTemperature: Double,
    val relativeHumidity: Double,
    val precipitationProbability: Double,
    val precipitation: Double,
    val rain: Double,
    val showers: Double,
    val snowfall: Double,
    val snowDepth: Double,
    val pressureMsl: Double,
    val cloudCover: Double,
    val visibility: Double,
    val windSpeed10m: Double,
    val windGusts10m: Double,
    val windDirection10m: Double,
    val weatherCode: Int,
)