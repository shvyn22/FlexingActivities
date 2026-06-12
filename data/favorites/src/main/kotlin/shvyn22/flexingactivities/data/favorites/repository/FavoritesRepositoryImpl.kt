package shvyn22.flexingactivities.data.favorites.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import shvyn22.flexingactivities.data.favorites.dao.FavoritesLocalDataSource
import shvyn22.flexingactivities.data.favorites.model.toDomain
import shvyn22.flexingactivities.data.favorites.model.toEntity
import shvyn22.flexingactivities.domain.favorites.model.FavoriteLocation
import shvyn22.flexingactivities.domain.favorites.repository.FavoritesRepository

internal class FavoritesRepositoryImpl(
    private val favoritesLocalDataSource: FavoritesLocalDataSource,
) : FavoritesRepository {

    override fun getFavoritesObservable(): Flow<List<FavoriteLocation>> {
        return favoritesLocalDataSource.getFavoritesObservable().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getById(id: Long): FavoriteLocation? {
        return favoritesLocalDataSource.getById(id)?.toDomain()
    }

    override fun getFavoriteByCoordinatesObservable(latitude: Double, longitude: Double): Flow<FavoriteLocation?> {
        return favoritesLocalDataSource.getByCoordinatesObservable(latitude, longitude).map { it?.toDomain() }
    }

    override suspend fun getByCoordinates(latitude: Double, longitude: Double): FavoriteLocation? {
        return favoritesLocalDataSource.getByCoordinates(latitude, longitude)?.toDomain()
    }

    override suspend fun save(location: FavoriteLocation) {
        favoritesLocalDataSource.upsert(location.toEntity())
    }

    override suspend fun delete(id: Long) {
        favoritesLocalDataSource.deleteById(id)
    }
}