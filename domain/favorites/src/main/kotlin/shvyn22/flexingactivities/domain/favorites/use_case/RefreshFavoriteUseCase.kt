package shvyn22.flexingactivities.domain.favorites.use_case

import kotlinx.coroutines.CoroutineDispatcher
import shvyn22.flexingactivities.domain.core.resource.Resource
import shvyn22.flexingactivities.domain.core.resource.ResourceError
import shvyn22.flexingactivities.domain.core.resource.executeUseCase
import shvyn22.flexingactivities.domain.favorites.repository.FavoritesRepository
import shvyn22.flexingactivities.domain.weather.use_case.GetLocationRankingUseCase

class RefreshFavoriteUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val favoritesRepository: FavoritesRepository,
    private val getLocationRanking: GetLocationRankingUseCase,
) {

    suspend operator fun invoke(id: Long): Resource<Unit> {
        return executeUseCase(dispatcher) {
            val favorite = favoritesRepository.getById(id)
                ?: return@executeUseCase Resource.Error(ResourceError.NotFound)

            when (val result = getLocationRanking(favorite.latitude, favorite.longitude)) {
                is Resource.Success -> {
                    favoritesRepository.save(
                        favorite.copy(
                            scores = result.data.rankings.associate { it.activity to it.overall },
                            updatedAt = System.currentTimeMillis(),
                        )
                    )
                    Resource.Success(Unit)
                }
                is Resource.Error -> Resource.Error(result.error)
            }
        }
    }
}
