package shvyn22.flexingactivities.domain.favorites.di

import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import shvyn22.flexingactivities.domain.favorites.use_case.DeleteFavoriteUseCase
import shvyn22.flexingactivities.domain.favorites.use_case.GetFavoriteByCoordinatesUseCase
import shvyn22.flexingactivities.domain.favorites.use_case.GetFavoritesUseCase
import shvyn22.flexingactivities.domain.favorites.use_case.RefreshFavoriteUseCase
import shvyn22.flexingactivities.domain.favorites.use_case.SaveFavoriteUseCase
import shvyn22.flexingactivities.domain.favorites.use_case.ToggleFavoriteUseCase

val FavoritesDomainModule = module {
    factoryOf(::GetFavoritesUseCase)
    factoryOf(::GetFavoriteByCoordinatesUseCase)
    factory<SaveFavoriteUseCase> { SaveFavoriteUseCase(get(named("io")), get()) }
    factory<DeleteFavoriteUseCase> { DeleteFavoriteUseCase(get(named("io")), get()) }
    factory<ToggleFavoriteUseCase> { ToggleFavoriteUseCase(get(named("io")), get()) }
    factory<RefreshFavoriteUseCase> { RefreshFavoriteUseCase(get(named("io")), get(), get()) }
}