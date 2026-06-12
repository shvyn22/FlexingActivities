package shvyn22.flexingactivities.feature.details

import androidx.compose.runtime.Stable
import shvyn22.flexingactivities.domain.weather.model.ActivityRanking
import shvyn22.flexingactivities.feature.core.mvi.MviEvent
import shvyn22.flexingactivities.feature.core.mvi.MviIntent
import shvyn22.flexingactivities.feature.core.mvi.MviState
import shvyn22.flexingactivities.feature.core.mvi.MviStateReducer

enum class DetailsMessage {
    ADDED_TO_FAVORITES,
    REMOVED_FROM_FAVORITES,
    FAILED_TO_TOGGLE_FAVORITES,
}

@Stable
data class DetailsState(
    val locationName: String = "",
    val rankings: List<ActivityRanking> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isFavorite: Boolean = false,
    val isError: Boolean = false,
    val notification: DetailsMessage? = null,
) : MviState

sealed interface DetailsIntent : MviIntent {
    data object LoadData : DetailsIntent
    data object CancelLoad : DetailsIntent
    data object Refresh : DetailsIntent
    data object ToggleFavorite : DetailsIntent
    data object DismissNotification : DetailsIntent
}

sealed interface DetailsEvent : MviEvent

sealed interface DetailsAction {
    data object NavigateBack : DetailsAction
}

fun onDetailsLoading(loading: Boolean) =
    MviStateReducer<DetailsState> { it.copy(isLoading = loading, isError = false) }

fun onDetailsLoaded(rankings: List<ActivityRanking>) =
    MviStateReducer<DetailsState> { it.copy(rankings = rankings, isLoading = false, isRefreshing = false, isError = false) }

fun onDetailsError() =
    MviStateReducer<DetailsState> { it.copy(isError = true, isLoading = false, isRefreshing = false) }

fun onDetailsRefreshing(refreshing: Boolean) =
    MviStateReducer<DetailsState> { it.copy(isRefreshing = refreshing) }

fun onFavoriteStatusUpdate(isFavorite: Boolean) =
    MviStateReducer<DetailsState> { it.copy(isFavorite = isFavorite) }

fun onLocationNameUpdate(name: String) =
    MviStateReducer<DetailsState> { it.copy(locationName = name) }

fun onNotification(message: DetailsMessage) =
    MviStateReducer<DetailsState> { it.copy(notification = message) }

fun onNotificationDismissed() =
    MviStateReducer<DetailsState> { it.copy(notification = null) }
