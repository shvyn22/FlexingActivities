package shvyn22.flexingactivities.domain.geocoding.use_case

import kotlinx.coroutines.CoroutineDispatcher
import shvyn22.flexingactivities.domain.core.resource.Resource
import shvyn22.flexingactivities.domain.core.resource.ResourceError
import shvyn22.flexingactivities.domain.core.resource.executeUseCase
import shvyn22.flexingactivities.domain.geocoding.model.GeoLocation
import shvyn22.flexingactivities.domain.geocoding.repository.GeocodingRepository

class SearchLocationsUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val geocodingRepository: GeocodingRepository,
) {

    suspend operator fun invoke(query: String): Resource<List<GeoLocation>> {
        return executeUseCase(dispatcher) {
            val results = geocodingRepository.search(query)

            if (results.isEmpty())
                Resource.Error(ResourceError.NotFound)
            else
                Resource.Success(results)
        }
    }
}