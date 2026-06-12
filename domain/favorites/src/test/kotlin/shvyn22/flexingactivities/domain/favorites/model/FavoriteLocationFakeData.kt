package shvyn22.flexingactivities.domain.favorites.model

import shvyn22.flexingactivities.domain.core.model.Activity

internal val testFavorite = FavoriteLocation(
    id = 1L,
    name = "Berlin",
    country = "Germany",
    latitude = 52.52,
    longitude = 13.405,
    scores = mapOf(
        Activity.SKIING to 10,
        Activity.SURFING to 30,
        Activity.OUTDOOR_SIGHTSEEING to 75,
        Activity.INDOOR_SIGHTSEEING to 20,
    ),
    updatedAt = 1_700_000_000_000L,
)

internal val testFavorite2 = FavoriteLocation(
    id = 2L,
    name = "Vienna",
    country = "Austria",
    latitude = 48.2082,
    longitude = 16.3738,
    scores = mapOf(
        Activity.SKIING to 5,
        Activity.SURFING to 15,
        Activity.OUTDOOR_SIGHTSEEING to 80,
        Activity.INDOOR_SIGHTSEEING to 25,
    ),
    updatedAt = 1_700_000_001_000L,
)
