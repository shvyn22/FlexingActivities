package shvyn22.flexingactivities.domain.weather.repository

import shvyn22.flexingactivities.domain.weather.model.HourlyWeather
import java.io.IOException

class FakeWeatherRepository(
    var shouldFailNetwork: Boolean = false,
) : WeatherRepository {

    var forecast: HourlyWeather? = null

    override suspend fun getForecast(
        latitude: Double,
        longitude: Double,
    ): HourlyWeather {
        if (shouldFailNetwork) throw IOException("Simulated network failure")
        return checkNotNull(forecast) { "FakeWeatherRepository.forecast is not set" }
    }
}
