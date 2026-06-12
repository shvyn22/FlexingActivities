package shvyn22.flexingactivities.domain.favorites.use_case

import kotlinx.coroutines.flow.Flow
import shvyn22.flexingactivities.domain.favorites.model.FavoriteLocation
import shvyn22.flexingactivities.domain.favorites.repository.FavoritesRepository

class GetFavoritesUseCase(
    private val favoritesRepository: FavoritesRepository,
) {

    operator fun invoke(): Flow<List<FavoriteLocation>> {
        return favoritesRepository.getFavoritesObservable()
    }
}