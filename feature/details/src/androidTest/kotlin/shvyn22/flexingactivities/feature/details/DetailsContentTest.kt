package shvyn22.flexingactivities.feature.details

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
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
import shvyn22.flexingactivities.domain.weather.model.ActivityRanking
import shvyn22.flexingactivities.domain.weather.model.DailyScore
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class DetailsContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loading_state_shows_progress_indicator() {
        composeTestRule.setContent {
            AppTheme {
                DetailsContent(
                    state = DetailsState(isLoading = true),
                    onIntent = {},
                    onAction = {},
                )
            }
        }

        composeTestRule
            .onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertIsDisplayed()
    }

    @Test
    fun error_state_shows_error_text() {
        composeTestRule.setContent {
            AppTheme {
                DetailsContent(
                    state = DetailsState(isLoading = false, isError = true),
                    onIntent = {},
                    onAction = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Something went wrong").assertIsDisplayed()
    }

    @Test
    fun loaded_state_shows_location_name_in_top_bar() {
        composeTestRule.setContent {
            AppTheme {
                DetailsContent(
                    state = DetailsState(
                        locationName = "Berlin",
                        rankings = sampleRankings,
                    ),
                    onIntent = {},
                    onAction = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Berlin").assertIsDisplayed()
    }

    @Test
    fun loaded_state_shows_all_ranking_activities() {
        composeTestRule.setContent {
            AppTheme {
                DetailsContent(
                    state = DetailsState(isLoading = false, rankings = sampleRankings),
                    onIntent = {},
                    onAction = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Skiing").assertIsDisplayed()
        composeTestRule.onNodeWithText("Surfing").assertIsDisplayed()
        composeTestRule.onNodeWithText("Outdoor Sightseeing").assertIsDisplayed()
        composeTestRule.onNodeWithText("Indoor Sightseeing").assertIsDisplayed()
    }

    @Test
    fun favorite_toggle_invokes_ToggleFavorite_intent() {
        val intents = mutableListOf<DetailsIntent>()

        composeTestRule.setContent {
            AppTheme {
                DetailsContent(
                    state = DetailsState(isLoading = false, rankings = sampleRankings, isFavorite = false),
                    onIntent = { intents.add(it) },
                    onAction = {},
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Add to favorites").performClick()

        assertTrue(intents.any { it is DetailsIntent.ToggleFavorite })
    }

    @Test
    fun remove_favorite_shows_correct_content_description_when_is_favorite() {
        composeTestRule.setContent {
            AppTheme {
                DetailsContent(
                    state = DetailsState(isLoading = false, rankings = sampleRankings, isFavorite = true),
                    onIntent = {},
                    onAction = {},
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Remove from favorites").assertIsDisplayed()
    }

    @Test
    fun fallback_title_is_shown_when_location_name_is_blank() {
        composeTestRule.setContent {
            AppTheme {
                DetailsContent(
                    state = DetailsState(locationName = ""),
                    onIntent = {},
                    onAction = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Details").assertIsDisplayed()
    }

    companion object {
        private val sampleRankings = listOf(
            ActivityRanking(
                activity = Activity.SKIING,
                daily = listOf(DailyScore(LocalDate.of(2026, 1, 15), 20)),
                overall = 20,
            ),
            ActivityRanking(
                activity = Activity.SURFING,
                daily = listOf(DailyScore(LocalDate.of(2026, 1, 15), 50)),
                overall = 50,
            ),
            ActivityRanking(
                activity = Activity.OUTDOOR_SIGHTSEEING,
                daily = listOf(DailyScore(LocalDate.of(2026, 1, 15), 80)),
                overall = 80,
            ),
            ActivityRanking(
                activity = Activity.INDOOR_SIGHTSEEING,
                daily = listOf(DailyScore(LocalDate.of(2026, 1, 15), 30)),
                overall = 30,
            ),
        )
    }
}
