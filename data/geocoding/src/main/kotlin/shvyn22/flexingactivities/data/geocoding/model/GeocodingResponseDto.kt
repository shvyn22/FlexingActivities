package shvyn22.flexingactivities.data.geocoding.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GeocodingResponseDto(
    @SerialName("results") val results: List<GeoLocationDto>? = null,
)

@Serializable
internal data class GeoLocationDto(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("country") val country: String? = null,
    @SerialName("country_code") val countryCode: String? = null,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("admin1") val admin1: String? = null,
    @SerialName("timezone") val timezone: String? = null,
)