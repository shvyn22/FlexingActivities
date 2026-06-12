package shvyn22.flexingactivities.data.weather.data_source

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import shvyn22.flexingactivities.data.weather.model.ForecastResponseDto

internal class WeatherRemoteDataSource(
    private val client: HttpClient,
    private val baseUrl: String,
) {
    suspend fun getForecast(
        latitude: Double,
        longitude: Double,
    ): ForecastResponseDto {
        return client.get("$baseUrl/forecast") {
            parameter("latitude", latitude)
            parameter("longitude", longitude)
            parameter("hourly", HOURLY_PARAMS)
            parameter("wind_speed_unit", "ms")
            parameter("forecast_days", 7)
        }.body()
    }

    companion object {
        private const val HOURLY_PARAMS =
            "temperature_2m,apparent_temperature,relative_humidity_2m," +
                    "precipitation_probability,precipitation,rain,showers,snowfall,snow_depth," +
                    "pressure_msl,cloud_cover,visibility,wind_speed_10m,wind_gusts_10m," +
                    "wind_direction_10m,weather_code"
    }
}