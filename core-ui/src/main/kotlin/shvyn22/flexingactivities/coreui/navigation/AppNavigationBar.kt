package shvyn22.flexingactivities.coreui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import shvyn22.flexingactivities.coreui.R
import shvyn22.flexingactivities.coreui.theme.AppTheme

enum class AppTab { SEARCH, FAVORITES }

@Composable
fun AppNavigationBar(
    currentTab: AppTab,
    onSearchClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(modifier = modifier) {
        NavigationBarItem(
            selected = currentTab == AppTab.SEARCH,
            onClick = onSearchClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.cd_search),
                )
            },
            label = { Text(stringResource(R.string.label_search)) },
        )
        NavigationBarItem(
            selected = currentTab == AppTab.FAVORITES,
            onClick = onFavoritesClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = stringResource(R.string.cd_favorites),
                )
            },
            label = { Text(stringResource(R.string.label_favorites)) },
        )
    }
}

@PreviewLightDark
@Composable
private fun AppNavigationBarSearchSelectedPreview() {
    AppTheme {
        Box {
            AppNavigationBar(
                currentTab = AppTab.SEARCH,
                onSearchClick = {},
                onFavoritesClick = {},
            )
        }
    }
}