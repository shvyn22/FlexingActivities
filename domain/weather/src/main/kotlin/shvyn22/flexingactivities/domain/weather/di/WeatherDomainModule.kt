package shvyn22.flexingactivities.domain.weather.di

import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import shvyn22.flexingactivities.domain.weather.use_case.GetActivityResolversUseCase
import shvyn22.flexingactivities.domain.weather.use_case.GetLocationRankingUseCase

val WeatherDomainModule = module {
    factoryOf(::GetActivityResolversUseCase)
    factory<GetLocationRankingUseCase> {
        GetLocationRankingUseCase(
            dispatcher = get(named("default")),
            weatherRepository = get(),
            getResolvers = get(),
        )
    }
}