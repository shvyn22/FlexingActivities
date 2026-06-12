package shvyn22.flexingactivities.domain.favorites.use_case

import kotlinx.coroutines.CoroutineDispatcher
import shvyn22.flexingactivities.domain.core.resource.Resource
import shvyn22.flexingactivities.domain.core.resource.executeUseCase
import shvyn22.flexingactivities.domain.favorites.model.FavoriteLocation
import shvyn22.flexingactivities.domain.favorites.repository.FavoritesRepository

class SaveFavoriteUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val favoritesRepository: FavoritesRepository,
) {

    suspend operator fun invoke(location: FavoriteLocation): Resource<Unit> {
        return executeUseCase(dispatcher) {
            favoritesRepository.save(location)
            Resource.Success(Unit)
        }
    }
}