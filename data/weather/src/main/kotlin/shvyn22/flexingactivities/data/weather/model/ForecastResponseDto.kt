package shvyn22.flexingactivities.data.weather.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ForecastResponseDto(
    @SerialName("timezone") val timezone: String,
    @SerialName("utc_offset_seconds") val utcOffsetSeconds: Int,
    @SerialName("hourly") val hourly: HourlyDto,
)

@Serializable
internal data class HourlyDto(
    @SerialName("time") val time: List<String>,
    @SerialName("temperature_2m") val temperature2m: List<Double>,
    @SerialName("apparent_temperature") val apparentTemperature: List<Double>,
    @SerialName("relative_humidity_2m") val relativeHumidity2m: List<Double>,
    @SerialName("precipitation_probability") val precipitationProbability: List<Double>,
    @SerialName("precipitation") val precipitation: List<Double>,
    @SerialName("rain") val rain: List<Double>,
    @SerialName("showers") val showers: List<Double>,
    @SerialName("snowfall") val snowfall: List<Double>,
    @SerialName("snow_depth") val snowDepth: List<Double>,
    @SerialName("pressure_msl") val pressureMsl: List<Double>,
    @SerialName("cloud_cover") val cloudCover: List<Double>,
    @SerialName("visibility") val visibility: List<Double>,
    @SerialName("wind_speed_10m") val windSpeed10m: List<Double>,
    @SerialName("wind_gusts_10m") val windGusts10m: List<Double>,
    @SerialName("wind_direction_10m") val windDirection10m: List<Double>,
    @SerialName("weather_code") val weatherCode: List<Int>,
)