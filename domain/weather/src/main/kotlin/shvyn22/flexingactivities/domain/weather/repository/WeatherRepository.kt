package shvyn22.flexingactivities.domain.weather.repository

import shvyn22.flexingactivities.domain.weather.model.HourlyWeather

interface WeatherRepository {
    suspend fun getForecast(
        latitude: Double,
        longitude: Double
    ): HourlyWeather
}