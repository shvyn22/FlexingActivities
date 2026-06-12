package shvyn22.flexingactivities.domain.weather.use_case

import kotlinx.coroutines.CoroutineDispatcher
import shvyn22.flexingactivities.domain.core.resource.Resource
import shvyn22.flexingactivities.domain.core.resource.executeUseCase
import shvyn22.flexingactivities.domain.weather.model.LocationRanking
import shvyn22.flexingactivities.domain.weather.repository.WeatherRepository

class GetLocationRankingUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val weatherRepository: WeatherRepository,
    private val getResolvers: GetActivityResolversUseCase,
) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
    ): Resource<LocationRanking> {
        return executeUseCase(dispatcher) {
            val hourly = weatherRepository.getForecast(latitude, longitude)
            val rankings = getResolvers().map { it.resolve(hourly) }
            Resource.Success(LocationRanking(rankings))
        }
    }
}
