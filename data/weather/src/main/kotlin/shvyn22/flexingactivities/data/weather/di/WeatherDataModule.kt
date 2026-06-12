package shvyn22.flexingactivities.data.weather.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import shvyn22.flexingactivities.data.weather.data_source.WeatherRemoteDataSource
import shvyn22.flexingactivities.data.weather.repository.WeatherRepositoryImpl
import shvyn22.flexingactivities.data.weather.utils.BASE_URL
import shvyn22.flexingactivities.domain.weather.repository.WeatherRepository

val WeatherDataModule = module {
    single<WeatherRemoteDataSource> { WeatherRemoteDataSource(get(), BASE_URL) }
    singleOf(::WeatherRepositoryImpl) bind WeatherRepository::class
}
