package shvyn22.flexingactivities.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import shvyn22.flexingactivities.domain.core.resource.Resource
import shvyn22.flexingactivities.domain.favorites.model.FavoriteLocation
import shvyn22.flexingactivities.domain.favorites.use_case.DeleteFavoriteUseCase
import shvyn22.flexingactivities.domain.favorites.use_case.GetFavoritesUseCase
import shvyn22.flexingactivities.domain.favorites.use_case.RefreshFavoriteUseCase
import shvyn22.flexingactivities.feature.core.mvi.MviDelegate
import shvyn22.flexingactivities.feature.core.mvi.MviDelegateImpl

class FavoritesViewModel(
    private val getFavorites: GetFavoritesUseCase,
    private val deleteFavorite: DeleteFavoriteUseCase,
    private val refreshFavorite: RefreshFavoriteUseCase,
) : ViewModel(),
    MviDelegate<FavoritesState, FavoritesIntent, FavoritesEvent> by MviDelegateImpl(
        FavoritesState()
    ) {

    init {
        handleIntent(FavoritesIntent.LoadData)
    }

    override fun handleIntent(intent: FavoritesIntent) {
        when (intent) {
            is FavoritesIntent.LoadData -> loadData()
            is FavoritesIntent.RefreshAll -> refreshAll()
            is FavoritesIntent.RefreshItem -> refreshItem(intent.id)
            is FavoritesIntent.DeleteItem -> deleteItem(intent.id)
            is FavoritesIntent.NavigateToDetails -> navigateToDetails(intent.location)
        }
    }

    private fun loadData() {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            getFavorites().collect { list ->
                reduceState(onFavorites(list))
            }
        }
    }

    private fun refreshAll() {
        viewModelScope.launch {
            reduceState(onRefreshing(true))
            val ids = state.value.favorites.map { it.id }
            ids.forEach { id -> refreshFavorite(id) }
            reduceState(onRefreshing(false))
        }
    }

    private fun refreshItem(id: Long) {
        viewModelScope.launch {
            reduceState(onRefreshing(true))
            when (refreshFavorite(id)) {
                is Resource.Success -> reduceState(onRefreshing(false))
                is Resource.Error -> reduceState(onFavoritesError())
            }
        }
    }

    private fun deleteItem(id: Long) {
        viewModelScope.launch {
            when (deleteFavorite(id)) {
                is Resource.Success -> Unit
                is Resource.Error -> reduceState(onFavoritesError())
            }
        }
    }

    private fun navigateToDetails(location: FavoriteLocation) {
        sendEvent(
            FavoritesEvent.NavigateToDetails(
                location.latitude,
                location.longitude,
                location.name
            )
        )
    }
}
