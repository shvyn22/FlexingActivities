package shvyn22.flexingactivities.data.geocoding.repository

import shvyn22.flexingactivities.data.geocoding.data_source.GeocodingRemoteDataSource
import shvyn22.flexingactivities.data.geocoding.model.toDomain
import shvyn22.flexingactivities.domain.geocoding.model.GeoLocation
import shvyn22.flexingactivities.domain.geocoding.repository.GeocodingRepository

internal class GeocodingRepositoryImpl(
    private val geocodingRemoteDataSource: GeocodingRemoteDataSource,
) : GeocodingRepository {

    override suspend fun search(
        name: String,
        count: Int,
        language: String,
    ): List<GeoLocation> {
        val response = geocodingRemoteDataSource.search(name, count, language)
        return response.results?.map { it.toDomain() } ?: emptyList()
    }
}