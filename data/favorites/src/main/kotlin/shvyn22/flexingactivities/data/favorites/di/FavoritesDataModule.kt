package shvyn22.flexingactivities.data.favorites.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import shvyn22.flexingactivities.data.favorites.repository.FavoritesRepositoryImpl
import shvyn22.flexingactivities.domain.favorites.repository.FavoritesRepository

val FavoritesDataModule = module {
    singleOf(::FavoritesRepositoryImpl) bind FavoritesRepository::class
}
