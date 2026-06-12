package shvyn22.flexingactivities.feature.details.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import shvyn22.flexingactivities.feature.details.DetailsViewModel

val DetailsModule = module {
    viewModel<DetailsViewModel> { (latitude: Double, longitude: Double, name: String) ->
        DetailsViewModel(
            latitude = latitude,
            longitude = longitude,
            locationName = name,
            getRankingUseCase = get(),
            getFavoriteByCoordinatesUseCase = get(),
            toggleFavoriteUseCase = get(),
        )
    }
}