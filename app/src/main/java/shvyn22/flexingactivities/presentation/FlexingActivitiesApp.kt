package shvyn22.flexingactivities.presentation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import shvyn22.flexingactivities.feature.details.DetailsAction
import shvyn22.flexingactivities.feature.details.DetailsScreen
import shvyn22.flexingactivities.feature.favorites.FavoritesAction
import shvyn22.flexingactivities.feature.favorites.FavoritesScreen
import shvyn22.flexingactivities.feature.search.SearchAction
import shvyn22.flexingactivities.feature.search.SearchScreen

sealed interface Route {
    @Serializable data object Search : Route
    @Serializable data object Favorites : Route
    @Serializable data class Details(val latitude: Double, val longitude: Double, val name: String) : Route
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.Search,
        modifier = modifier,
    ) {
        composable<Route.Search>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) {
            SearchScreen(
                onAction = { action ->
                    when (action) {
                        is SearchAction.NavigateToDetails ->
                            navController.navigate(Route.Details(action.latitude, action.longitude, action.name))

                        is SearchAction.NavigateToFavorites ->
                            navController.navigate(Route.Favorites) {
                                popUpTo(Route.Search) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                    }
                },
            )
        }
        composable<Route.Favorites>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) {
            FavoritesScreen(
                onAction = { action ->
                    when (action) {
                        is FavoritesAction.NavigateToDetails ->
                            navController.navigate(Route.Details(action.latitude, action.longitude, action.name))

                        is FavoritesAction.NavigateToSearch ->
                            navController.navigate(Route.Search) {
                                popUpTo(Route.Search) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                    }
                },
            )
        }
        composable<Route.Details> { entry ->
            val route = entry.toRoute<Route.Details>()

            DetailsScreen(
                latitude = route.latitude,
                longitude = route.longitude,
                name = route.name,
                onAction = { action ->
                    when (action) {
                        is DetailsAction.NavigateBack -> navController.popBackStack()
                    }
                },
            )
        }
    }
}
