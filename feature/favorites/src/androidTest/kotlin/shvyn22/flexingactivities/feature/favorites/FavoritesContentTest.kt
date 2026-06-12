package shvyn22.flexingactivities.feature.favorites

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import shvyn22.flexingactivities.coreui.theme.AppTheme
import shvyn22.flexingactivities.domain.core.model.Activity
import shvyn22.flexingactivities.domain.favorites.model.FavoriteLocation

@RunWith(AndroidJUnit4::class)
class FavoritesContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun empty_state_shows_empty_hint() {
        composeTestRule.setContent {
            AppTheme {
                FavoritesContent(
                    state = FavoritesState(favorites = emptyList()),
                    onIntent = {},
                    onAction = {},
                )
            }
        }

        composeTestRule.onNodeWithText("No saved locations yet").assertIsDisplayed()
    }

    @Test
    fun favorites_list_shows_location_names() {
        composeTestRule.setContent {
            AppTheme {
                FavoritesContent(
                    state = FavoritesState(favorites = listOf(berlinFavorite, viennaFavorite)),
                    onIntent = {},
                    onAction = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Berlin").assertIsDisplayed()
        composeTestRule.onNodeWithText("Vienna").assertIsDisplayed()
    }

    @Test
    fun error_state_shows_error_text() {
        composeTestRule.setContent {
            AppTheme {
                FavoritesContent(
                    state = FavoritesState(isError = true),
                    onIntent = {},
                    onAction = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Something went wrong").assertIsDisplayed()
    }

    @Test
    fun delete_button_invokes_DeleteItem_intent() {
        val intents = mutableListOf<FavoritesIntent>()

        composeTestRule.setContent {
            AppTheme {
                FavoritesContent(
                    state = FavoritesState(favorites = listOf(berlinFavorite)),
                    onIntent = { intents.add(it) },
                    onAction = {},
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Delete").performClick()

        assertTrue(intents.any { it is FavoritesIntent.DeleteItem })
        val deleteIntent = intents.first { it is FavoritesIntent.DeleteItem } as FavoritesIntent.DeleteItem
        assertTrue(deleteIntent.id == berlinFavorite.id)
    }

    @Test
    fun refresh_button_invokes_RefreshItem_intent() {
        val intents = mutableListOf<FavoritesIntent>()

        composeTestRule.setContent {
            AppTheme {
                FavoritesContent(
                    state = FavoritesState(favorites = listOf(berlinFavorite)),
                    onIntent = { intents.add(it) },
                    onAction = {},
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Refresh").performClick()

        assertTrue(intents.any { it is FavoritesIntent.RefreshItem })
        val refreshIntent = intents.first { it is FavoritesIntent.RefreshItem } as FavoritesIntent.RefreshItem
        assertTrue(refreshIntent.id == berlinFavorite.id)
    }

    @Test
    fun clicking_card_invokes_NavigateToDetails_intent() {
        val intents = mutableListOf<FavoritesIntent>()

        composeTestRule.setContent {
            AppTheme {
                FavoritesContent(
                    state = FavoritesState(favorites = listOf(berlinFavorite)),
                    onIntent = { intents.add(it) },
                    onAction = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Berlin").performClick()

        assertTrue(intents.any { it is FavoritesIntent.NavigateToDetails })
    }

    companion object {
        private val defaultScores = mapOf(
            Activity.SKIING to 10,
            Activity.SURFING to 50,
            Activity.OUTDOOR_SIGHTSEEING to 75,
            Activity.INDOOR_SIGHTSEEING to 20,
        )

        private val berlinFavorite = FavoriteLocation(
            id = 1L,
            name = "Berlin",
            country = "Germany",
            latitude = 52.52,
            longitude = 13.405,
            scores = defaultScores,
            updatedAt = 1_700_000_000_000L,
        )

        private val viennaFavorite = FavoriteLocation(
            id = 2L,
            name = "Vienna",
            country = "Austria",
            latitude = 48.2082,
            longitude = 16.3738,
            scores = defaultScores,
            updatedAt = 1_700_000_001_000L,
        )
    }
}
