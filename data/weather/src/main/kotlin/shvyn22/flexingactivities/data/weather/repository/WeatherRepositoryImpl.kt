package shvyn22.flexingactivities.data.weather.repository

import shvyn22.flexingactivities.data.weather.data_source.WeatherRemoteDataSource
import shvyn22.flexingactivities.data.weather.model.toDomain
import shvyn22.flexingactivities.domain.weather.model.HourlyWeather
import shvyn22.flexingactivities.domain.weather.repository.WeatherRepository

internal class WeatherRepositoryImpl(
    private val remoteDataSource: WeatherRemoteDataSource,
) : WeatherRepository {

    override suspend fun getForecast(
        latitude: Double,
        longitude: Double,
    ): HourlyWeather {
        return remoteDataSource.getForecast(latitude, longitude).toDomain()
    }
}