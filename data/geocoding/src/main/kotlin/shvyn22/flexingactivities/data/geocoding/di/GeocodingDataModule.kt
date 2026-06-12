package shvyn22.flexingactivities.data.geocoding.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import shvyn22.flexingactivities.data.geocoding.data_source.GeocodingRemoteDataSource
import shvyn22.flexingactivities.data.geocoding.repository.GeocodingRepositoryImpl
import shvyn22.flexingactivities.data.geocoding.utils.BASE_URL
import shvyn22.flexingactivities.domain.geocoding.repository.GeocodingRepository

val GeocodingDataModule = module {
    single<GeocodingRemoteDataSource> { GeocodingRemoteDataSource(get(), BASE_URL) }
    singleOf(::GeocodingRepositoryImpl) bind GeocodingRepository::class
}
