package shvyn22.flexingactivities.feature.search

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import shvyn22.flexingactivities.coreui.components.AppButton
import shvyn22.flexingactivities.coreui.components.AppCard
import shvyn22.flexingactivities.coreui.components.EmptyContent
import shvyn22.flexingactivities.coreui.components.ErrorContent
import shvyn22.flexingactivities.coreui.components.LoadingContent
import shvyn22.flexingactivities.coreui.navigation.AppNavigationBar
import shvyn22.flexingactivities.coreui.navigation.AppTab
import shvyn22.flexingactivities.coreui.navigation.AppTopBar
import shvyn22.flexingactivities.coreui.theme.AppTheme
import shvyn22.flexingactivities.domain.geocoding.model.GeoLocation
import shvyn22.flexingactivities.feature.core.util.ObservableSideEffect
import shvyn22.flexingactivities.feature.core.R as CoreR

@Composable
fun SearchScreen(
    onAction: (SearchAction) -> Unit,
) {
    val viewModel = koinViewModel<SearchViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    ObservableSideEffect(viewModel.event) { event ->
        when (event) {
            is SearchEvent.NavigateToDetails ->
                onAction(
                    SearchAction.NavigateToDetails(
                        event.latitude,
                        event.longitude,
                        event.name
                    )
                )
            SearchEvent.DismissKeyboard -> keyboardController?.hide()
        }
    }

    SearchContent(
        state = state,
        onIntent = viewModel::handleIntent,
        onAction = onAction,
    )
}

private enum class SearchContentState {
    LOADING,
    ERROR_NETWORK,
    ERROR_INVALID_COORDS,
    EMPTY,
    RESULTS,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchContent(
    state: SearchState,
    onIntent: (SearchIntent) -> Unit,
    onAction: (SearchAction) -> Unit,
) {
    Scaffold(
        topBar = {
            AppTopBar(title = stringResource(R.string.search_title))
        },
        bottomBar = {
            AppNavigationBar(
                currentTab = AppTab.SEARCH,
                onSearchClick = {},
                onFavoritesClick = { onAction(SearchAction.NavigateToFavorites) },
            )
        },
    ) { contentPadding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { onIntent(SearchIntent.Refresh) },
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = state.mode == SearchMode.NAME,
                        onClick = { if (state.mode != SearchMode.NAME) onIntent(SearchIntent.ToggleMode) },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    ) {
                        Text(stringResource(R.string.search_mode_name))
                    }
                    SegmentedButton(
                        selected = state.mode == SearchMode.COORDS,
                        onClick = { if (state.mode != SearchMode.COORDS) onIntent(SearchIntent.ToggleMode) },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    ) {
                        Text(stringResource(R.string.search_mode_coords))
                    }
                }

                Spacer(Modifier.size(8.dp))

                if (state.mode == SearchMode.NAME) {
                    OutlinedTextField(
                        value = state.query,
                        onValueChange = { onIntent(SearchIntent.UpdateQuery(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.search_field_location_name)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { onIntent(SearchIntent.Submit) }),
                    )
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = state.latInput,
                            onValueChange = { onIntent(SearchIntent.UpdateLatitude(it)) },
                            modifier = Modifier.weight(1f),
                            label = { Text(stringResource(R.string.search_field_latitude)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            ),
                        )
                        OutlinedTextField(
                            value = state.lonInput,
                            onValueChange = { onIntent(SearchIntent.UpdateLongitude(it)) },
                            modifier = Modifier.weight(1f),
                            label = { Text(stringResource(R.string.search_field_longitude)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Go,
                            ),
                            keyboardActions = KeyboardActions(
                                onGo = { onIntent(SearchIntent.Submit) }
                            ),
                        )
                    }
                }

                Spacer(Modifier.size(12.dp))

                AppButton(
                    text = stringResource(R.string.search_button),
                    onClick = { onIntent(SearchIntent.Submit) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading && if (state.mode == SearchMode.NAME) {
                        state.query.isNotBlank()
                    } else {
                        state.latInput.isNotBlank() && state.lonInput.isNotBlank()
                    },
                )

                Spacer(Modifier.size(16.dp))

                val contentState = when {
                    state.isLoading -> SearchContentState.LOADING
                    state.error == SearchError.NETWORK -> SearchContentState.ERROR_NETWORK
                    state.error == SearchError.INVALID_COORDINATES -> SearchContentState.ERROR_INVALID_COORDS
                    state.results.isEmpty() -> SearchContentState.EMPTY
                    else -> SearchContentState.RESULTS
                }

                AnimatedContent(
                    targetState = contentState,
                    label = "SearchContent",
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                ) { target ->
                    when (target) {
                        SearchContentState.LOADING -> LoadingContent()
                        SearchContentState.ERROR_NETWORK ->
                            ErrorContent(stringResource(CoreR.string.error_unknown))
                        SearchContentState.ERROR_INVALID_COORDS ->
                            ErrorContent(stringResource(R.string.error_invalid_coordinates))
                        SearchContentState.EMPTY ->
                            EmptyContent(stringResource(R.string.search_empty_hint))
                        SearchContentState.RESULTS ->
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                items(
                                    items = state.results,
                                    key = { it.id },
                                ) { location ->
                                    SearchResultItem(
                                        location = location,
                                        modifier = Modifier.animateItem(),
                                        onClick = { onIntent(SearchIntent.NavigateToDetails(location)) },
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
private fun SearchResultItem(
    location: GeoLocation,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    AppCard(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Text(
            text = location.name,
            style = MaterialTheme.typography.titleMedium,
        )
        val subtitle = listOfNotNull(location.admin1, location.country).joinToString(", ")
        if (subtitle.isNotBlank()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun SearchNamePreview() {
    AppTheme {
        Box {
            SearchContent(
                state = SearchState(
                    mode = SearchMode.NAME,
                ),
                onIntent = {},
                onAction = {},
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun SearchCoordsPreview() {
    AppTheme {
        Box {
            SearchContent(
                state = SearchState(
                    mode = SearchMode.COORDS,
                ),
                onIntent = {},
                onAction = {},
            )
        }
    }
}