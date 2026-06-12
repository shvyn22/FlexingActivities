package shvyn22.flexingactivities.feature.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import shvyn22.flexingactivities.domain.core.resource.Resource
import shvyn22.flexingactivities.domain.favorites.use_case.GetFavoriteByCoordinatesUseCase
import shvyn22.flexingactivities.domain.favorites.use_case.ToggleFavoriteUseCase
import shvyn22.flexingactivities.domain.weather.use_case.GetLocationRankingUseCase
import shvyn22.flexingactivities.feature.core.mvi.MviDelegate
import shvyn22.flexingactivities.feature.core.mvi.MviDelegateImpl
import kotlin.time.Duration.Companion.milliseconds

class DetailsViewModel(
    private val latitude: Double,
    private val longitude: Double,
    private val locationName: String,
    private val getRankingUseCase: GetLocationRankingUseCase,
    private val getFavoriteByCoordinatesUseCase: GetFavoriteByCoordinatesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : ViewModel(),
    MviDelegate<DetailsState, DetailsIntent, DetailsEvent> by MviDelegateImpl(
        DetailsState(
            locationName = locationName
        )
    ) {

    private var rankingJob: Job? = null
    private var favoriteStatusJob: Job? = null
    private var notificationJob: Job? = null

    override fun handleIntent(intent: DetailsIntent) {
        when (intent) {
            is DetailsIntent.LoadData -> loadData()
            is DetailsIntent.CancelLoad -> cancelLoad()
            is DetailsIntent.Refresh -> refresh()
            is DetailsIntent.ToggleFavorite -> toggleFavorite()
            is DetailsIntent.DismissNotification -> dismissNotification()
        }
    }

    private fun loadData() {
        loadRanking()
        loadFavoriteStatus()
    }

    private fun cancelLoad() {
        rankingJob?.cancel()
        rankingJob = null
        favoriteStatusJob?.cancel()
        favoriteStatusJob = null
    }

    private fun refresh() {
        rankingJob?.cancel()
        rankingJob = viewModelScope.launch {
            reduceState(onDetailsRefreshing(true))

            getRanking()
        }
    }

    private fun loadRanking() {
        rankingJob?.cancel()
        rankingJob = viewModelScope.launch {
            reduceState(onDetailsLoading(true))

            getRanking()
        }
    }

    private suspend fun getRanking() {
        when (val result = getRankingUseCase(latitude, longitude)) {
            is Resource.Success -> reduceState(onDetailsLoaded(result.data.rankings))
            is Resource.Error -> reduceState(onDetailsError())
        }
    }

    private fun loadFavoriteStatus() {
        favoriteStatusJob?.cancel()
        favoriteStatusJob = viewModelScope.launch {
            getFavoriteByCoordinatesUseCase(latitude, longitude).collect { favorite ->
                reduceState(onFavoriteStatusUpdate(favorite != null))
            }
        }
    }

    private fun toggleFavorite() {
        viewModelScope.launch {
            val scores = state.value.rankings.associate { it.activity to it.overall }
            when (
                val result = toggleFavoriteUseCase(latitude, longitude, locationName, scores)
            ) {
                is Resource.Success ->
                    showNotification(
                        if (result.data) DetailsMessage.ADDED_TO_FAVORITES
                        else DetailsMessage.REMOVED_FROM_FAVORITES
                    )

                is Resource.Error ->
                    showNotification(DetailsMessage.FAILED_TO_TOGGLE_FAVORITES)

                null -> return@launch
            }
        }
    }

    private fun showNotification(message: DetailsMessage) {
        notificationJob?.cancel()
        reduceState(onNotification(message))
        notificationJob = viewModelScope.launch {
            delay(3_000.milliseconds)
            reduceState(onNotificationDismissed())
        }
    }

    private fun dismissNotification() {
        notificationJob?.cancel()
        notificationJob = null
        reduceState(onNotificationDismissed())
    }
}
