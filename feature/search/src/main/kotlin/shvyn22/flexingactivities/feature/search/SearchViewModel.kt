package shvyn22.flexingactivities.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import shvyn22.flexingactivities.domain.core.resource.Resource
import shvyn22.flexingactivities.domain.geocoding.model.GeoLocation
import shvyn22.flexingactivities.domain.geocoding.use_case.SearchLocationsUseCase
import shvyn22.flexingactivities.feature.core.mvi.MviDelegate
import shvyn22.flexingactivities.feature.core.mvi.MviDelegateImpl

class SearchViewModel(
    private val searchLocations: SearchLocationsUseCase,
) : ViewModel(),
    MviDelegate<SearchState, SearchIntent, SearchEvent> by MviDelegateImpl(
        SearchState()
    ) {

    override fun handleIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.ToggleMode -> toggleMode()
            is SearchIntent.UpdateQuery -> updateQuery(intent.query)
            is SearchIntent.UpdateLatitude -> updateLatitude(intent.value)
            is SearchIntent.UpdateLongitude -> updateLongitude(intent.value)
            is SearchIntent.Submit -> submit()
            is SearchIntent.Refresh -> refresh()
            is SearchIntent.NavigateToDetails -> navigateToDetails(intent.location)
        }
    }

    private fun toggleMode() {
        reduceState(onModeToggle())
    }

    private fun updateQuery(query: String) {
        reduceState(onQueryChange(query))
    }

    private fun updateLatitude(value: String) {
        reduceState(onLatChange(value))
    }

    private fun updateLongitude(value: String) {
        reduceState(onLonChange(value))
    }

    private fun submit() {
        sendEvent(SearchEvent.DismissKeyboard)
        val currentState = state.value
        if (currentState.mode == SearchMode.NAME)
            searchByName(currentState.query)
        else
            searchByCoords(currentState.latInput, currentState.lonInput)
    }

    private fun refresh() {
        val currentState = state.value
        if (currentState.mode == SearchMode.NAME) {
            refreshByName(currentState.query)
        }
    }

    private fun refreshByName(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            reduceState(onSearchRefreshing(true))

            searchLocationsByName(query)
        }
    }

    private fun searchByName(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            reduceState(onSearchLoading(true))

            searchLocationsByName(query)
        }
    }

    private suspend fun searchLocationsByName(query: String) {
        when (val result = searchLocations(query)) {
            is Resource.Success -> reduceState(onSearchResults(result.data))
            is Resource.Error -> reduceState(onSearchError(SearchError.NETWORK))
        }
    }

    private fun searchByCoords(
        latitudeQuery: String,
        longitudeQuery: String,
    ) {
        val latitude = latitudeQuery.toDoubleOrNull()
        val longitude = longitudeQuery.toDoubleOrNull()

        if (
            latitude == null ||
            longitude == null ||
            latitude !in -90.0..90.0 ||
            longitude !in -180.0..180.0
        ) {
            reduceState(onSearchError(SearchError.INVALID_COORDINATES))
            return
        }

        sendEvent(SearchEvent.NavigateToDetails(latitude, longitude, "$latitude, $longitude"))
    }

    private fun navigateToDetails(location: GeoLocation) {
        sendEvent(
            SearchEvent.NavigateToDetails(
                location.latitude,
                location.longitude,
                location.name
            )
        )
    }
}
