package shvyn22.flexingactivities.feature.favorites

import app.cash.turbine.test
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
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
import shvyn22.flexingactivities.domain.core.model.Activity
import shvyn22.flexingactivities.domain.favorites.model.FavoriteLocation
import shvyn22.flexingactivities.domain.favorites.repository.FakeFavoritesRepository
import shvyn22.flexingactivities.domain.favorites.use_case.DeleteFavoriteUseCase
import shvyn22.flexingactivities.domain.favorites.use_case.GetFavoritesUseCase
import shvyn22.flexingactivities.domain.favorites.use_case.RefreshFavoriteUseCase
import shvyn22.flexingactivities.domain.weather.model.HourlyPoint
import shvyn22.flexingactivities.domain.weather.model.HourlyWeather
import shvyn22.flexingactivities.domain.weather.repository.FakeWeatherRepository
import shvyn22.flexingactivities.domain.weather.use_case.GetActivityResolversUseCase
import shvyn22.flexingactivities.domain.weather.use_case.GetLocationRankingUseCase
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeFavoritesRepository
    private lateinit var weatherRepository: FakeWeatherRepository
    private lateinit var viewModel: FavoritesViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeFavoritesRepository()
        weatherRepository = FakeWeatherRepository()
        viewModel = buildViewModel()
        viewModel.handleIntent(FavoritesIntent.LoadData)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `LoadData loads empty favorites list`() {
        assertTrue(viewModel.state.value.favorites.isEmpty())
        assertFalse(viewModel.state.value.isError)
    }

    @Test
    fun `LoadData loads seeded favorites`() = runTest {
        repository.save(berlinFavorite)
        val vm = buildViewModel()
        vm.handleIntent(FavoritesIntent.LoadData)

        assertEquals(listOf(berlinFavorite), vm.state.value.favorites)
    }

    @Test
    fun `favorites update reactively when repository changes`() = runTest {
        repository.save(berlinFavorite)

        assertEquals(listOf(berlinFavorite), viewModel.state.value.favorites)
    }

    @Test
    fun `DeleteItem removes the favorite`() = runTest {
        repository.save(berlinFavorite)
        val vm = buildViewModel()
        vm.handleIntent(FavoritesIntent.LoadData)

        vm.handleIntent(FavoritesIntent.DeleteItem(berlinFavorite.id))

        assertTrue(vm.state.value.favorites.isEmpty())
    }

    @Test
    fun `RefreshItem success clears refreshing state`() = runTest {
        repository.save(berlinFavorite)
        weatherRepository.forecast = testHourlyWeather
        val vm = buildViewModel()
        vm.handleIntent(FavoritesIntent.LoadData)

        vm.handleIntent(FavoritesIntent.RefreshItem(berlinFavorite.id))

        assertFalse(vm.state.value.isRefreshing)
        assertFalse(vm.state.value.isError)
    }

    @Test
    fun `RefreshItem sets isError when refresh fails`() = runTest {
        repository.save(berlinFavorite)
        weatherRepository.shouldFailNetwork = true
        val vm = buildViewModel()
        vm.handleIntent(FavoritesIntent.LoadData)

        vm.handleIntent(FavoritesIntent.RefreshItem(berlinFavorite.id))

        assertTrue(vm.state.value.isError)
        assertFalse(vm.state.value.isRefreshing)
    }

    @Test
    fun `DeleteItem sets isError when delete fails`() = runTest {
        repository.save(berlinFavorite)
        repository.shouldThrow = true
        val vm = buildViewModel()
        vm.handleIntent(FavoritesIntent.LoadData)

        vm.handleIntent(FavoritesIntent.DeleteItem(berlinFavorite.id))

        assertTrue(vm.state.value.isError)
    }

    @Test
    fun `RefreshAll success clears refreshing state`() = runTest {
        repository.save(berlinFavorite)
        repository.save(viennaFavorite)
        weatherRepository.forecast = testHourlyWeather
        val vm = buildViewModel()
        vm.handleIntent(FavoritesIntent.LoadData)

        vm.handleIntent(FavoritesIntent.RefreshAll)

        assertFalse(vm.state.value.isRefreshing)
        assertFalse(vm.state.value.isError)
    }

    @Test
    fun `NavigateToDetails emits NavigateToDetails event`() = runTest {
        repository.save(berlinFavorite)
        val vm = buildViewModel()
        vm.handleIntent(FavoritesIntent.LoadData)

        vm.event.test {
            vm.handleIntent(FavoritesIntent.NavigateToDetails(berlinFavorite))
            val event = awaitItem()
            assertTrue(event is FavoritesEvent.NavigateToDetails)
            event as FavoritesEvent.NavigateToDetails
            assertEquals(berlinFavorite.latitude, event.latitude)
            assertEquals(berlinFavorite.longitude, event.longitude)
            assertEquals(berlinFavorite.name, event.name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun buildViewModel(): FavoritesViewModel {
        return FavoritesViewModel(
            getFavorites = GetFavoritesUseCase(repository),
            deleteFavorite = DeleteFavoriteUseCase(testDispatcher, repository),
            refreshFavorite = RefreshFavoriteUseCase(
                testDispatcher,
                repository,
                GetLocationRankingUseCase(testDispatcher, weatherRepository, GetActivityResolversUseCase()),
            ),
        )
    }

    companion object {
        private val defaultScores = mapOf(
            Activity.SKIING to 10,
            Activity.SURFING to 30,
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

        private val testHourlyWeather = HourlyWeather(
            points = listOf(
                HourlyPoint(
                    time = LocalDateTime.of(2024, 1, 15, 12, 0),
                    temperature = 15.0,
                    apparentTemperature = 15.0,
                    relativeHumidity = 60.0,
                    precipitationProbability = 10.0,
                    precipitation = 0.0,
                    rain = 0.0,
                    showers = 0.0,
                    snowfall = 0.0,
                    snowDepth = 0.0,
                    pressureMsl = 1013.0,
                    cloudCover = 30.0,
                    visibility = 20000.0,
                    windSpeed10m = 3.0,
                    windGusts10m = 5.0,
                    windDirection10m = 180.0,
                    weatherCode = 0,
                )
            ),
            timezone = "UTC",
            utcOffsetSeconds = 0,
        )
    }
}
