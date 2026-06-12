package shvyn22.flexingactivities.domain.favorites.use_case

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import shvyn22.flexingactivities.domain.core.model.Activity
import shvyn22.flexingactivities.domain.core.resource.Resource
import shvyn22.flexingactivities.domain.core.resource.executeUseCase
import shvyn22.flexingactivities.domain.favorites.model.FavoriteLocation
import shvyn22.flexingactivities.domain.favorites.repository.FavoritesRepository

class ToggleFavoriteUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val repository: FavoritesRepository,
) {

    private val mutex = Mutex()

    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        locationName: String,
        scores: Map<Activity, Int>,
    ): Resource<Boolean>? {
        if (!mutex.tryLock()) return null
        return try {
            executeUseCase(dispatcher) {
                val existing = repository.getByCoordinates(latitude, longitude)
                if (existing != null) {
                    repository.delete(existing.id)
                    Resource.Success(false)
                } else {
                    repository.save(
                        FavoriteLocation(
                            name = locationName,
                            country = "",
                            latitude = latitude,
                            longitude = longitude,
                            scores = scores,
                            updatedAt = System.currentTimeMillis(),
                        )
                    )
                    Resource.Success(true)
                }
            }
        } finally {
            mutex.unlock()
        }
    }
}
