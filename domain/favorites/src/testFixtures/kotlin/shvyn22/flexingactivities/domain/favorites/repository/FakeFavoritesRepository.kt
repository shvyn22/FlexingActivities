package shvyn22.flexingactivities.domain.favorites.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import shvyn22.flexingactivities.domain.favorites.model.FavoriteLocation

class FakeFavoritesRepository : FavoritesRepository {

    var shouldThrow: Boolean = false

    private val _favorites = MutableStateFlow<List<FavoriteLocation>>(emptyList())
    private var nextId = 1L

    override fun getFavoritesObservable(): Flow<List<FavoriteLocation>> {
        return _favorites
    }

    override suspend fun getById(id: Long): FavoriteLocation? {
        return _favorites.value.find { it.id == id }
    }

    override fun getFavoriteByCoordinatesObservable(
        latitude: Double,
        longitude: Double,
    ): Flow<FavoriteLocation?> {
        return _favorites.map { list ->
            list.find { it.latitude == latitude && it.longitude == longitude }
        }
    }

    override suspend fun getByCoordinates(
        latitude: Double,
        longitude: Double,
    ): FavoriteLocation? {
        return _favorites.value.find { it.latitude == latitude && it.longitude == longitude }
    }

    override suspend fun save(location: FavoriteLocation) {
        if (shouldThrow) throw Exception("Simulated failure")
        val existingIndex = _favorites.value.indexOfFirst { it.id == location.id }
        _favorites.value = if (existingIndex >= 0) {
            _favorites.value.toMutableList().also { it[existingIndex] = location }
        } else {
            val assignedId = if (location.id == 0L) nextId++ else location.id
            _favorites.value + location.copy(id = assignedId)
        }
    }

    override suspend fun delete(id: Long) {
        if (shouldThrow) throw Exception("Simulated failure")
        _favorites.value = _favorites.value.filter { it.id != id }
    }
}
