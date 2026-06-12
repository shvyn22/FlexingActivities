package shvyn22.flexingactivities.domain.geocoding.repository

import shvyn22.flexingactivities.domain.geocoding.model.GeoLocation
import java.io.IOException

class FakeGeocodingRepository(
    var shouldFailNetwork: Boolean = false,
) : GeocodingRepository {

    private val locations = mutableMapOf<String, GeoLocation>()

    fun register(vararg geoLocations: GeoLocation) {
        geoLocations.forEach { locations[it.name] = it }
    }

    override suspend fun search(
        name: String,
        count: Int,
        language: String,
    ): List<GeoLocation> {
        if (shouldFailNetwork) throw IOException("Simulated network failure")
        return locations.values.filter { it.name.contains(name, ignoreCase = true) }
    }
}
