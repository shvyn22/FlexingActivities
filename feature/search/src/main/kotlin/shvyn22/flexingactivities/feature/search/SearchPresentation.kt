package shvyn22.flexingactivities.feature.search

import androidx.compose.runtime.Stable
import shvyn22.flexingactivities.domain.geocoding.model.GeoLocation
import shvyn22.flexingactivities.feature.core.mvi.MviEvent
import shvyn22.flexingactivities.feature.core.mvi.MviIntent
import shvyn22.flexingactivities.feature.core.mvi.MviState
import shvyn22.flexingactivities.feature.core.mvi.MviStateReducer

enum class SearchMode { NAME, COORDS }

enum class SearchError { NETWORK, INVALID_COORDINATES }

@Stable
data class SearchState(
    val query: String = "",
    val mode: SearchMode = SearchMode.NAME,
    val latInput: String = "",
    val lonInput: String = "",
    val results: List<GeoLocation> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: SearchError? = null,
) : MviState

sealed interface SearchIntent : MviIntent {
    data object ToggleMode : SearchIntent
    data class UpdateQuery(val query: String) : SearchIntent
    data class UpdateLatitude(val value: String) : SearchIntent
    data class UpdateLongitude(val value: String) : SearchIntent
    data object Submit : SearchIntent
    data object Refresh : SearchIntent
    data class NavigateToDetails(val location: GeoLocation) : SearchIntent
}

sealed interface SearchEvent : MviEvent {
    data class NavigateToDetails(
        val latitude: Double,
        val longitude: Double,
        val name: String,
    ) : SearchEvent

    data object DismissKeyboard : SearchEvent
}

sealed interface SearchAction {
    data class NavigateToDetails(
        val latitude: Double,
        val longitude: Double,
        val name: String,
    ) : SearchAction

    data object NavigateToFavorites : SearchAction
}

fun onSearchLoading(isLoading: Boolean) =
    MviStateReducer<SearchState> { it.copy(isLoading = isLoading, error = null) }

fun onSearchRefreshing(isRefreshing: Boolean) =
    MviStateReducer<SearchState> { it.copy(isRefreshing = isRefreshing, error = null) }

fun onSearchResults(results: List<GeoLocation>) =
    MviStateReducer<SearchState> { it.copy(results = results, isLoading = false, isRefreshing = false, error = null) }

fun onSearchError(error: SearchError) =
    MviStateReducer<SearchState> { it.copy(error = error, isLoading = false, isRefreshing = false) }

fun onQueryChange(query: String) =
    MviStateReducer<SearchState> { it.copy(query = query) }

fun onModeToggle() =
    MviStateReducer<SearchState> {
        it.copy(
            mode = it.mode.toggled(),
            query = "",
            latInput = "",
            lonInput = "",
            results = emptyList(),
            error = null
        )
    }

fun onLatChange(value: String) =
    MviStateReducer<SearchState> { it.copy(latInput = value) }

fun onLonChange(value: String) =
    MviStateReducer<SearchState> { it.copy(lonInput = value) }

fun SearchMode.toggled(): SearchMode {
    return if (this == SearchMode.NAME) SearchMode.COORDS else SearchMode.NAME
}
