package shvyn22.flexingactivities.feature.search

import app.cash.turbine.test
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import shvyn22.flexingactivities.domain.geocoding.model.GeoLocation
import shvyn22.flexingactivities.domain.geocoding.repository.FakeGeocodingRepository
import shvyn22.flexingactivities.domain.geocoding.use_case.SearchLocationsUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeGeocodingRepository
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeGeocodingRepository()
        viewModel = SearchViewModel(SearchLocationsUseCase(testDispatcher, repository))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has default values`() {
        val state = viewModel.state.value
        assertEquals("", state.query)
        assertEquals(SearchMode.NAME, state.mode)
        assertTrue(state.results.isEmpty())
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `UpdateQuery intent updates query in state`() {
        viewModel.handleIntent(SearchIntent.UpdateQuery("Berlin"))

        assertEquals("Berlin", viewModel.state.value.query)
    }

    @Test
    fun `ToggleMode switches from NAME to COORDS and clears query and results`() {
        repository.register(berlinLocation)
        viewModel.handleIntent(SearchIntent.UpdateQuery("Berlin"))
        viewModel.handleIntent(SearchIntent.Submit)

        viewModel.handleIntent(SearchIntent.ToggleMode)

        val state = viewModel.state.value
        assertEquals(SearchMode.COORDS, state.mode)
        assertEquals("", state.query)
        assertTrue(state.results.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun `ToggleMode switches from COORDS back to NAME and clears coord inputs and error`() {
        viewModel.handleIntent(SearchIntent.ToggleMode)
        viewModel.handleIntent(SearchIntent.UpdateLatitude("abc"))
        viewModel.handleIntent(SearchIntent.UpdateLongitude("13.405"))
        viewModel.handleIntent(SearchIntent.Submit)

        viewModel.handleIntent(SearchIntent.ToggleMode)

        val state = viewModel.state.value
        assertEquals(SearchMode.NAME, state.mode)
        assertEquals("", state.latInput)
        assertEquals("", state.lonInput)
        assertNull(state.error)
    }

    @Test
    fun `Submit in NAME mode with results updates state`() = runTest {
        repository.register(berlinLocation)
        viewModel.handleIntent(SearchIntent.UpdateQuery("Berlin"))

        viewModel.handleIntent(SearchIntent.Submit)

        val state = viewModel.state.value
        assertEquals(listOf(berlinLocation), state.results)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `Submit in NAME mode with empty results sets NETWORK error`() = runTest {
        viewModel.handleIntent(SearchIntent.UpdateQuery("unknownplace"))

        viewModel.handleIntent(SearchIntent.Submit)

        val state = viewModel.state.value
        assertEquals(SearchError.NETWORK, state.error)
        assertFalse(state.isLoading)
    }

    @Test
    fun `Submit in NAME mode with blank query does nothing`() = runTest {
        viewModel.handleIntent(SearchIntent.UpdateQuery(""))

        viewModel.handleIntent(SearchIntent.Submit)

        val state = viewModel.state.value
        assertTrue(state.results.isEmpty())
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `Submit in NAME mode with network failure sets NETWORK error`() = runTest {
        repository.shouldFailNetwork = true
        viewModel.handleIntent(SearchIntent.UpdateQuery("Berlin"))

        viewModel.handleIntent(SearchIntent.Submit)

        assertEquals(SearchError.NETWORK, viewModel.state.value.error)
    }

    @Test
    fun `Submit in COORDS mode with valid coords emits NavigateToDetails event`() = runTest {
        viewModel.handleIntent(SearchIntent.ToggleMode)
        viewModel.handleIntent(SearchIntent.UpdateLatitude("52.52"))
        viewModel.handleIntent(SearchIntent.UpdateLongitude("13.405"))

        viewModel.event.test {
            viewModel.handleIntent(SearchIntent.Submit)
            skipItems(1) // DismissKeyboard
            val event = awaitItem()
            assertTrue(event is SearchEvent.NavigateToDetails)
            event as SearchEvent.NavigateToDetails
            assertEquals(52.52, event.latitude)
            assertEquals(13.405, event.longitude)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Submit in NAME mode emits DismissKeyboard event`() = runTest {
        repository.register(berlinLocation)
        viewModel.handleIntent(SearchIntent.UpdateQuery("Berlin"))

        viewModel.event.test {
            viewModel.handleIntent(SearchIntent.Submit)
            val event = awaitItem()
            assertTrue(event is SearchEvent.DismissKeyboard)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Submit in COORDS mode with invalid latitude sets INVALID_COORDINATES error`() = runTest {
        viewModel.handleIntent(SearchIntent.ToggleMode)
        viewModel.handleIntent(SearchIntent.UpdateLatitude("999.0"))
        viewModel.handleIntent(SearchIntent.UpdateLongitude("13.405"))

        viewModel.handleIntent(SearchIntent.Submit)

        assertEquals(SearchError.INVALID_COORDINATES, viewModel.state.value.error)
    }

    @Test
    fun `Submit in COORDS mode with non-numeric input sets INVALID_COORDINATES error`() = runTest {
        viewModel.handleIntent(SearchIntent.ToggleMode)
        viewModel.handleIntent(SearchIntent.UpdateLatitude("abc"))
        viewModel.handleIntent(SearchIntent.UpdateLongitude("13.405"))

        viewModel.handleIntent(SearchIntent.Submit)

        assertEquals(SearchError.INVALID_COORDINATES, viewModel.state.value.error)
    }

    @Test
    fun `Submit in COORDS mode with invalid longitude sets INVALID_COORDINATES error`() = runTest {
        viewModel.handleIntent(SearchIntent.ToggleMode)
        viewModel.handleIntent(SearchIntent.UpdateLatitude("52.52"))
        viewModel.handleIntent(SearchIntent.UpdateLongitude("999.0"))

        viewModel.handleIntent(SearchIntent.Submit)

        assertEquals(SearchError.INVALID_COORDINATES, viewModel.state.value.error)
    }

    @Test
    fun `Refresh in NAME mode with results updates state`() = runTest {
        repository.register(berlinLocation)
        viewModel.handleIntent(SearchIntent.UpdateQuery("Berlin"))

        viewModel.handleIntent(SearchIntent.Refresh)

        val state = viewModel.state.value
        assertEquals(listOf(berlinLocation), state.results)
        assertFalse(state.isRefreshing)
        assertNull(state.error)
    }

    @Test
    fun `Refresh in NAME mode with blank query does nothing`() = runTest {
        viewModel.handleIntent(SearchIntent.Refresh)

        val state = viewModel.state.value
        assertTrue(state.results.isEmpty())
        assertFalse(state.isRefreshing)
        assertNull(state.error)
    }

    @Test
    fun `Refresh in COORDS mode does nothing`() = runTest {
        viewModel.handleIntent(SearchIntent.ToggleMode)
        viewModel.handleIntent(SearchIntent.UpdateLatitude("52.52"))
        viewModel.handleIntent(SearchIntent.UpdateLongitude("13.405"))

        viewModel.handleIntent(SearchIntent.Refresh)

        val state = viewModel.state.value
        assertTrue(state.results.isEmpty())
        assertFalse(state.isRefreshing)
    }

    @Test
    fun `NavigateToDetails intent emits NavigateToDetails event`() = runTest {
        viewModel.event.test {
            viewModel.handleIntent(SearchIntent.NavigateToDetails(berlinLocation))
            val event = awaitItem()
            assertTrue(event is SearchEvent.NavigateToDetails)
            event as SearchEvent.NavigateToDetails
            assertEquals(berlinLocation.latitude, event.latitude)
            assertEquals(berlinLocation.longitude, event.longitude)
            assertEquals(berlinLocation.name, event.name)
            cancelAndIgnoreRemainingEvents()
        }
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
    }
}
