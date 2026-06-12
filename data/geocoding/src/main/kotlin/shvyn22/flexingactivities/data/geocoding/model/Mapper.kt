package shvyn22.flexingactivities.data.geocoding.model

import shvyn22.flexingactivities.domain.geocoding.model.GeoLocation

internal fun GeoLocationDto.toDomain(): GeoLocation {
    return GeoLocation(
        id = id,
        name = name,
        country = country ?: "",
        countryCode = countryCode ?: "",
        latitude = latitude,
        longitude = longitude,
        admin1 = admin1,
        timezone = timezone ?: "UTC",
    )
}