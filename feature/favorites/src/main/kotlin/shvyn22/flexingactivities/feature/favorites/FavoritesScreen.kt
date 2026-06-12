package shvyn22.flexingactivities.feature.favorites

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import shvyn22.flexingactivities.coreui.components.AppCard
import shvyn22.flexingactivities.coreui.components.EmptyContent
import shvyn22.flexingactivities.coreui.components.ErrorContent
import shvyn22.flexingactivities.coreui.components.ScoreBadge
import shvyn22.flexingactivities.coreui.components.resources.toBadgeLabel
import shvyn22.flexingactivities.coreui.navigation.AppNavigationBar
import shvyn22.flexingactivities.coreui.navigation.AppTab
import shvyn22.flexingactivities.coreui.navigation.AppTopBar
import shvyn22.flexingactivities.coreui.theme.AppTheme
import shvyn22.flexingactivities.coreui.utils.toFormattedDateTime
import shvyn22.flexingactivities.domain.core.model.Activity
import shvyn22.flexingactivities.domain.favorites.model.FavoriteLocation
import shvyn22.flexingactivities.feature.core.util.ObservableSideEffect
import shvyn22.flexingactivities.feature.core.R as CoreR

@Composable
fun FavoritesScreen(
    onAction: (FavoritesAction) -> Unit,
) {
    val viewModel = koinViewModel<FavoritesViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObservableSideEffect(viewModel.event) { event ->
        when (event) {
            is FavoritesEvent.NavigateToDetails ->
                onAction(
                    FavoritesAction.NavigateToDetails(
                        event.latitude,
                        event.longitude,
                        event.name
                    )
                )
        }
    }

    FavoritesContent(
        state = state,
        onIntent = viewModel::handleIntent,
        onAction = onAction,
    )
}

private enum class FavoritesContentState {
    ERROR,
    EMPTY,
    RESULTS,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesContent(
    state: FavoritesState,
    onIntent: (FavoritesIntent) -> Unit,
    onAction: (FavoritesAction) -> Unit,
) {
    Scaffold(
        topBar = {
            AppTopBar(title = stringResource(R.string.favorites_title))
        },
        bottomBar = {
            AppNavigationBar(
                currentTab = AppTab.FAVORITES,
                onSearchClick = { onAction(FavoritesAction.NavigateToSearch) },
                onFavoritesClick = {},
            )
        },
    ) { contentPadding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { onIntent(FavoritesIntent.RefreshAll) },
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                val contentState = when {
                    state.isError -> FavoritesContentState.ERROR
                    state.favorites.isEmpty() -> FavoritesContentState.EMPTY
                    else -> FavoritesContentState.RESULTS
                }

                AnimatedContent(
                    targetState = contentState,
                    modifier = Modifier.weight(1f),
                    label = "FavoritesContent",
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                ) { target ->
                    when (target) {
                        FavoritesContentState.ERROR ->
                            ErrorContent(stringResource(CoreR.string.error_unknown))
                        FavoritesContentState.EMPTY ->
                            EmptyContent(stringResource(R.string.favorites_empty))
                        FavoritesContentState.RESULTS ->
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                items(state.favorites, key = { it.id }) { fav ->
                                    FavoriteItem(
                                        location = fav,
                                        modifier = Modifier.animateItem(),
                                        onCardClick = { onIntent(FavoritesIntent.NavigateToDetails(fav)) },
                                        onRefreshClick = { onIntent(FavoritesIntent.RefreshItem(fav.id)) },
                                        onDeleteClick = { onIntent(FavoritesIntent.DeleteItem(fav.id)) },
                                    )
                                }
                            }
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteItem(
    location: FavoriteLocation,
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    AppCard(onClick = onCardClick, modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(location.name, style = MaterialTheme.typography.titleMedium)
                if (location.country.isNotBlank()) {
                    Text(
                        text = location.country,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            IconButton(onClick = onRefreshClick) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.favorites_cd_refresh),
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.favorites_cd_delete),
                )
            }
        }
        Spacer(Modifier.size(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            ScoreBadge(
                score = location.scores[Activity.SKIING] ?: 0,
                label = Activity.SKIING.toBadgeLabel(),
                modifier = Modifier.weight(1f),
            )
            ScoreBadge(
                score = location.scores[Activity.SURFING] ?: 0,
                label = Activity.SURFING.toBadgeLabel(),
                modifier = Modifier.weight(1f),
            )
            ScoreBadge(
                score = location.scores[Activity.OUTDOOR_SIGHTSEEING] ?: 0,
                label = Activity.OUTDOOR_SIGHTSEEING.toBadgeLabel(),
                modifier = Modifier.weight(1f),
            )
            ScoreBadge(
                score = location.scores[Activity.INDOOR_SIGHTSEEING] ?: 0,
                label = Activity.INDOOR_SIGHTSEEING.toBadgeLabel(),
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = stringResource(R.string.favorites_last_updated, location.updatedAt.toFormattedDateTime()),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@PreviewLightDark
@Composable
private fun FavoritesPreview() {
    AppTheme {
        Box {
            FavoritesContent(
                state = FavoritesState(
                    favorites = listOf(
                        FavoriteLocation(
                            id = 1,
                            name = "Valencia",
                            country = "Spain",
                            latitude = 48.2082,
                            longitude = 16.3738,
                            scores = mapOf(
                                Activity.SKIING to 0,
                                Activity.SURFING to 51,
                                Activity.OUTDOOR_SIGHTSEEING to 91,
                                Activity.INDOOR_SIGHTSEEING to 21,
                            ),
                            updatedAt = 0L,
                        )
                    ),
                ),
                onIntent = {},
                onAction = {},
            )
        }
    }
}
