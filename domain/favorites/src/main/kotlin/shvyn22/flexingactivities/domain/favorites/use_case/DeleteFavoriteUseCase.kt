package shvyn22.flexingactivities.domain.favorites.use_case

import kotlinx.coroutines.CoroutineDispatcher
import shvyn22.flexingactivities.domain.core.resource.Resource
import shvyn22.flexingactivities.domain.core.resource.executeUseCase
import shvyn22.flexingactivities.domain.favorites.repository.FavoritesRepository

class DeleteFavoriteUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val repository: FavoritesRepository,
) {

    suspend operator fun invoke(id: Long): Resource<Unit> {
        return executeUseCase(dispatcher) {
            repository.delete(id)
            Resource.Success(Unit)
        }
    }
}