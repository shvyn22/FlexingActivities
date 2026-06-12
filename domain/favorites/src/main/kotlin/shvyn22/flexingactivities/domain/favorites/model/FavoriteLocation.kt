package shvyn22.flexingactivities.domain.favorites.model

import shvyn22.flexingactivities.domain.core.model.Activity

data class FavoriteLocation(
    val id: Long = 0,
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val scores: Map<Activity, Int>,
    val updatedAt: Long,
)