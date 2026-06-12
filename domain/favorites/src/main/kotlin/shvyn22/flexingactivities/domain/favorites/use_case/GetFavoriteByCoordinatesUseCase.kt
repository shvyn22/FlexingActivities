package shvyn22.flexingactivities.domain.favorites.use_case

import kotlinx.coroutines.flow.Flow
import shvyn22.flexingactivities.domain.favorites.model.FavoriteLocation
import shvyn22.flexingactivities.domain.favorites.repository.FavoritesRepository

class GetFavoriteByCoordinatesUseCase(
    private val repository: FavoritesRepository,
) {

    operator fun invoke(latitude: Double, longitude: Double): Flow<FavoriteLocation?> {
        return repository.getFavoriteByCoordinatesObservable(
            latitude = latitude,
            longitude = longitude
        )
    }
}
