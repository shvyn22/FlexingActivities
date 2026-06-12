package shvyn22.flexingactivities.domain.geocoding.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import shvyn22.flexingactivities.domain.geocoding.use_case.SearchLocationsUseCase

val GeocodingDomainModule = module {
    factory<SearchLocationsUseCase> { SearchLocationsUseCase(get(named("io")), get()) }
}