package shvyn22.flexingactivities.domain.geocoding.repository

import shvyn22.flexingactivities.domain.geocoding.model.GeoLocation

interface GeocodingRepository {
    suspend fun search(
        name: String,
        count: Int = 10,
        language: String = "en",
    ): List<GeoLocation>
}