package shvyn22.flexingactivities.data.geocoding.data_source

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import shvyn22.flexingactivities.data.geocoding.model.GeocodingResponseDto

internal class GeocodingRemoteDataSource(
    private val client: HttpClient,
    private val baseUrl: String,
) {
    suspend fun search(
        name: String,
        count: Int,
        language: String,
    ): GeocodingResponseDto {
        return client.get("$baseUrl/search") {
            parameter("name", name)
            parameter("count", count)
            parameter("language", language)
            parameter("format", "json")
        }.body()
    }
}