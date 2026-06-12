package shvyn22.flexingactivities.feature.favorites.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import shvyn22.flexingactivities.feature.favorites.FavoritesViewModel

val FavoritesModule = module {
    viewModelOf(::FavoritesViewModel)
}