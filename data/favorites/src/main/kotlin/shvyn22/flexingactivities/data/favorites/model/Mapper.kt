package shvyn22.flexingactivities.data.favorites.model

import shvyn22.flexingactivities.domain.core.model.Activity
import shvyn22.flexingactivities.domain.favorites.model.FavoriteLocation

internal fun FavoriteLocationEntity.toDomain(): FavoriteLocation {
    return FavoriteLocation(
        id = id,
        name = name,
        country = country,
        latitude = latitude,
        longitude = longitude,
        scores = mapOf(
            Activity.SKIING to skiingScore,
            Activity.SURFING to surfingScore,
            Activity.OUTDOOR_SIGHTSEEING to outdoorScore,
            Activity.INDOOR_SIGHTSEEING to indoorScore,
        ),
        updatedAt = updatedAt,
    )
}

internal fun FavoriteLocation.toEntity(): FavoriteLocationEntity {
    return FavoriteLocationEntity(
        id = id,
        name = name,
        country = country,
        latitude = latitude,
        longitude = longitude,
        skiingScore = scores[Activity.SKIING] ?: 0,
        surfingScore = scores[Activity.SURFING] ?: 0,
        outdoorScore = scores[Activity.OUTDOOR_SIGHTSEEING] ?: 0,
        indoorScore = scores[Activity.INDOOR_SIGHTSEEING] ?: 0,
        updatedAt = updatedAt,
    )
}