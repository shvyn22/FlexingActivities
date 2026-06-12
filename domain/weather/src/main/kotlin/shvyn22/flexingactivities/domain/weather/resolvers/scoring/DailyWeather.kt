package shvyn22.flexingactivities.domain.weather.resolvers.scoring

import java.time.LocalDate

internal data class DailyWeather(
    val date: LocalDate,
    // daytime (06:00-21:00) means
    val temperature: Double,
    val apparentTemperature: Double,
    val relativeHumidity: Double,
    val precipitationProbability: Double,
    val pressureMsl: Double,
    val cloudCover: Double,
    val visibility: Double,
    val windSpeed10m: Double,
    val windGusts10m: Double,
    // full-day means (for Indoor)
    val temperatureAllDay: Double,
    val windSpeed10mAllDay: Double,
    // daily sums
    val precipitation: Double,
    val rain: Double,
    val showers: Double,
    val snowfall: Double,
    // daily max
    val snowDepth: Double,
    val weatherCode: Int,
)
