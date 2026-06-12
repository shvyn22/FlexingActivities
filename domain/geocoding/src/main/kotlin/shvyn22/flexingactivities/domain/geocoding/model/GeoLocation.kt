package shvyn22.flexingactivities.domain.geocoding.model

data class GeoLocation(
    val id: Long,
    val name: String,
    val country: String,
    val countryCode: String,
    val latitude: Double,
    val longitude: Double,
    val admin1: String?,
    val timezone: String,
)