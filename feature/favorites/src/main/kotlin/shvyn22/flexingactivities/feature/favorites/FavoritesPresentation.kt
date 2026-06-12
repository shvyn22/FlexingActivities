package shvyn22.flexingactivities.feature.favorites

import androidx.compose.runtime.Stable
import shvyn22.flexingactivities.domain.favorites.model.FavoriteLocation
import shvyn22.flexingactivities.feature.core.mvi.MviEvent
import shvyn22.flexingactivities.feature.core.mvi.MviIntent
import shvyn22.flexingactivities.feature.core.mvi.MviState
import shvyn22.flexingactivities.feature.core.mvi.MviStateReducer

@Stable
data class FavoritesState(
    val favorites: List<FavoriteLocation> = emptyList(),
    val isRefreshing: Boolean = false,
    val isError: Boolean = false,
) : MviState

sealed interface FavoritesIntent : MviIntent {
    data object LoadData : FavoritesIntent
    data object RefreshAll : FavoritesIntent
    data class RefreshItem(val id: Long) : FavoritesIntent
    data class DeleteItem(val id: Long) : FavoritesIntent
    data class NavigateToDetails(val location: FavoriteLocation) : FavoritesIntent
}

sealed interface FavoritesEvent : MviEvent {
    data class NavigateToDetails(
        val latitude: Double,
        val longitude: Double,
        val name: String,
    ) : FavoritesEvent
}

sealed interface FavoritesAction {
    data class NavigateToDetails(
        val latitude: Double,
        val longitude: Double,
        val name: String,
    ) : FavoritesAction

    data object NavigateToSearch : FavoritesAction
}

fun onFavorites(list: List<FavoriteLocation>) =
    MviStateReducer<FavoritesState> { it.copy(favorites = list, isError = false) }

fun onFavoritesError() =
    MviStateReducer<FavoritesState> { it.copy(isError = true, isRefreshing = false) }

fun onRefreshing(refreshing: Boolean) =
    MviStateReducer<FavoritesState> { it.copy(isRefreshing = refreshing) }
