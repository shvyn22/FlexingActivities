package shvyn22.flexingactivities.feature.search

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import shvyn22.flexingactivities.coreui.theme.AppTheme
import shvyn22.flexingactivities.domain.geocoding.model.GeoLocation

@RunWith(AndroidJUnit4::class)
class SearchContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loading_state_shows_progress_indicator() {
        composeTestRule.setContent {
            AppTheme {
                SearchContent(
                    state = SearchState(isLoading = true),
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
    fun empty_state_shows_empty_hint() {
        composeTestRule.setContent {
            AppTheme {
                SearchContent(
                    state = SearchState(results = emptyList()),
                    onIntent = {},
                    onAction = {},
                )
            }
        }

        composeTestRule
            .onNodeWithText("Search for a location to get started")
            .assertIsDisplayed()
    }

    @Test
    fun results_list_shows_location_names() {
        composeTestRule.setContent {
            AppTheme {
                SearchContent(
                    state = SearchState(results = listOf(berlinLocation, viennaLocation)),
                    onIntent = {},
                    onAction = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Berlin").assertIsDisplayed()
        composeTestRule.onNodeWithText("Vienna").assertIsDisplayed()
    }

    @Test
    fun clicking_result_invokes_NavigateToDetails_intent() {
        val intents = mutableListOf<SearchIntent>()

        composeTestRule.setContent {
            AppTheme {
                SearchContent(
                    state = SearchState(results = listOf(berlinLocation)),
                    onIntent = { intents.add(it) },
                    onAction = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Berlin").performClick()

        assertTrue(intents.any { it is SearchIntent.NavigateToDetails })
        assertEquals(
            berlinLocation,
            (intents.first { it is SearchIntent.NavigateToDetails } as SearchIntent.NavigateToDetails).location,
        )
    }

    @Test
    fun network_error_shows_error_text() {
        composeTestRule.setContent {
            AppTheme {
                SearchContent(
                    state = SearchState(error = SearchError.NETWORK),
                    onIntent = {},
                    onAction = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Something went wrong").assertIsDisplayed()
    }

    @Test
    fun invalid_coords_error_shows_error_text() {
        composeTestRule.setContent {
            AppTheme {
                SearchContent(
                    state = SearchState(
                        mode = SearchMode.COORDS,
                        error = SearchError.INVALID_COORDINATES,
                    ),
                    onIntent = {},
                    onAction = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Invalid coordinates").assertIsDisplayed()
    }

    @Test
    fun coords_mode_shows_latitude_and_longitude_fields() {
        composeTestRule.setContent {
            AppTheme {
                SearchContent(
                    state = SearchState(mode = SearchMode.COORDS),
                    onIntent = {},
                    onAction = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Latitude").assertIsDisplayed()
        composeTestRule.onNodeWithText("Longitude").assertIsDisplayed()
    }

    @Test
    fun name_mode_shows_location_name_field() {
        composeTestRule.setContent {
            AppTheme {
                SearchContent(
                    state = SearchState(mode = SearchMode.NAME),
                    onIntent = {},
                    onAction = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Location name").assertIsDisplayed()
    }

    companion object {
        private val berlinLocation = GeoLocation(
            id = 1L,
            name = "Berlin",
            country = "Germany",
            countryCode = "DE",
            latitude = 52.52,
            longitude = 13.405,
            admin1 = "Brandenburg",
            timezone = "Europe/Berlin",
        )

        private val viennaLocation = GeoLocation(
            id = 2L,
            name = "Vienna",
            country = "Austria",
            countryCode = "AT",
            latitude = 48.2082,
            longitude = 16.3738,
            admin1 = null,
            timezone = "Europe/Vienna",
        )
    }
}
