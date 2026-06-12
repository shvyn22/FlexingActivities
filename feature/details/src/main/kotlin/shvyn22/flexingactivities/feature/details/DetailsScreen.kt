package shvyn22.flexingactivities.feature.details

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import shvyn22.flexingactivities.coreui.components.AppCard
import shvyn22.flexingactivities.coreui.components.AppNotificationChip
import shvyn22.flexingactivities.coreui.components.ErrorContent
import shvyn22.flexingactivities.coreui.components.LoadingContent
import shvyn22.flexingactivities.coreui.components.ScoreBadge
import shvyn22.flexingactivities.coreui.components.actions.BackNavigationIcon
import shvyn22.flexingactivities.coreui.components.actions.FavoriteAction
import shvyn22.flexingactivities.coreui.components.resources.toDisplayString
import shvyn22.flexingactivities.coreui.components.scoreColor
import shvyn22.flexingactivities.coreui.navigation.AppTopBar
import shvyn22.flexingactivities.coreui.theme.AppTheme
import shvyn22.flexingactivities.coreui.utils.DatePattern
import shvyn22.flexingactivities.coreui.utils.format
import shvyn22.flexingactivities.domain.core.model.Activity
import shvyn22.flexingactivities.domain.weather.model.ActivityRanking
import shvyn22.flexingactivities.domain.weather.model.DailyScore
import shvyn22.flexingactivities.feature.core.R as CoreR

@Composable
fun DetailsScreen(
    latitude: Double,
    longitude: Double,
    name: String,
    onAction: (DetailsAction) -> Unit,
) {
    val viewModel = koinViewModel<DetailsViewModel>(
        parameters = { parametersOf(latitude, longitude, name) }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    LifecycleResumeEffect(Unit) {
        viewModel.handleIntent(DetailsIntent.LoadData)
        onPauseOrDispose {
            viewModel.handleIntent(DetailsIntent.CancelLoad)
        }
    }

    DetailsContent(
        state = state,
        onIntent = viewModel::handleIntent,
        onAction = onAction,
    )
}

private enum class DetailsContentState {
    LOADING,
    ERROR,
    RESULTS,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsContent(
    state: DetailsState,
    onIntent: (DetailsIntent) -> Unit,
    onAction: (DetailsAction) -> Unit,
) {
    val contentState = when {
        state.isLoading -> DetailsContentState.LOADING
        state.isError -> DetailsContentState.ERROR
        else -> DetailsContentState.RESULTS
    }

    Scaffold(
        snackbarHost = {
            state.notification?.let { notification ->
                AppNotificationChip(
                    message = notification.toDisplayString(),
                    onDismiss = { onIntent(DetailsIntent.DismissNotification) },
                )
            }
        },
        topBar = {
            AppTopBar(
                title = state.locationName.ifBlank { stringResource(R.string.details_title_fallback) },
                navigationIcon = {
                    BackNavigationIcon(
                        onClick = { onAction(DetailsAction.NavigateBack) }
                    )
                },
                actions = {
                    when (contentState) {
                        DetailsContentState.ERROR,
                        DetailsContentState.RESULTS -> FavoriteAction(
                            isFavorite = state.isFavorite,
                            onClick = { onIntent(DetailsIntent.ToggleFavorite) },
                        )

                        else -> Unit
                    }
                },
            )
        },
    ) { contentPadding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { onIntent(DetailsIntent.Refresh) },
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                AnimatedContent(
                    targetState = contentState,
                    modifier = Modifier.weight(1f),
                    label = "DetailsContent",
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                ) { target ->
                    when (target) {
                        DetailsContentState.LOADING -> LoadingContent()
                        DetailsContentState.ERROR ->
                            ErrorContent(stringResource(CoreR.string.error_unknown))
                        DetailsContentState.RESULTS ->
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(
                                    items = state.rankings,
                                    key = { it.activity.name }
                                ) { ranking ->
                                    ActivityRankingCard(
                                    ranking = ranking,
                                    modifier = Modifier.animateItem(),
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
private fun ActivityRankingCard(
    ranking: ActivityRanking,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    AppCard(
        onClick = { expanded = !expanded },
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = ranking.activity.toDisplayString(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            ScoreBadge(score = ranking.overall, label = null)
        }

        if (expanded && ranking.daily.isNotEmpty()) {
            Spacer(Modifier.size(8.dp))
            HorizontalDivider()
            Spacer(Modifier.size(8.dp))
            LazyRow {
                items(
                    items = ranking.daily,
                    key = { it.date },
                ) { daily ->
                    DailyScoreItem(
                        dailyScore = daily,
                        modifier = Modifier
                            .animateItem()
                            .padding(horizontal = 8.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyScoreItem(
    dailyScore: DailyScore,
    modifier: Modifier = Modifier,
) {
    AppCard(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = dailyScore.date.format(DatePattern.ABBREVIATED_WEEKDAY),
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = dailyScore.date.format(DatePattern.DAY_MONTH),
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = "${dailyScore.score}",
                style = MaterialTheme.typography.bodySmall,
                color = scoreColor(dailyScore.score),
            )
        }
    }
}

@Composable
private fun DetailsMessage.toDisplayString(): String {
    return stringResource(
        when (this) {
            DetailsMessage.ADDED_TO_FAVORITES -> R.string.details_added_to_favorites
            DetailsMessage.REMOVED_FROM_FAVORITES -> R.string.details_removed_from_favorites
            DetailsMessage.FAILED_TO_TOGGLE_FAVORITES -> R.string.details_failed_to_toggle_favorites
        }
    )
}

@PreviewLightDark
@Composable
private fun DetailsPreview() {
    AppTheme {
        Box {
            DetailsContent(
                state = DetailsState(
                    locationName = "Valencia",
                    rankings = listOf(
                        ActivityRanking(
                            activity = Activity.SKIING,
                            daily = emptyList(),
                            overall = 10,
                        ),
                        ActivityRanking(
                            activity = Activity.OUTDOOR_SIGHTSEEING,
                            daily = emptyList(),
                            overall = 88,
                        ),
                    ),
                ),
                onIntent = {},
                onAction = {},
            )
        }
    }
}
