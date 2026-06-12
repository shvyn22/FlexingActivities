package shvyn22.flexingactivities.domain.favorites.repository

import kotlinx.coroutines.flow.Flow
import shvyn22.flexingactivities.domain.favorites.model.FavoriteLocation

interface FavoritesRepository {
    fun getFavoritesObservable(): Flow<List<FavoriteLocation>>
    suspend fun getById(id: Long): FavoriteLocation?
    fun getFavoriteByCoordinatesObservable(latitude: Double, longitude: Double): Flow<FavoriteLocation?>
    suspend fun getByCoordinates(latitude: Double, longitude: Double): FavoriteLocation?
    suspend fun save(location: FavoriteLocation)
    suspend fun delete(id: Long)
}