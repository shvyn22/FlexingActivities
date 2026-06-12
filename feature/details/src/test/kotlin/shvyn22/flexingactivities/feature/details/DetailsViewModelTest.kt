package shvyn22.flexingactivities.feature.details

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
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
import shvyn22.flexingactivities.domain.core.model.Activity
import shvyn22.flexingactivities.domain.core.resource.Resource
import shvyn22.flexingactivities.domain.favorites.model.FavoriteLocation
import shvyn22.flexingactivities.domain.favorites.repository.FakeFavoritesRepository
import shvyn22.flexingactivities.domain.favorites.use_case.GetFavoriteByCoordinatesUseCase
import shvyn22.flexingactivities.domain.favorites.use_case.ToggleFavoriteUseCase
import shvyn22.flexingactivities.domain.weather.model.HourlyPoint
import shvyn22.flexingactivities.domain.weather.model.HourlyWeather
import shvyn22.flexingactivities.domain.weather.repository.FakeWeatherRepository
import shvyn22.flexingactivities.domain.weather.use_case.GetActivityResolversUseCase
import shvyn22.flexingactivities.domain.weather.use_case.GetLocationRankingUseCase
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var weatherRepository: FakeWeatherRepository
    private lateinit var favoritesRepository: FakeFavoritesRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        weatherRepository = FakeWeatherRepository()
        favoritesRepository = FakeFavoritesRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init sets location name in state`() {
        weatherRepository.forecast = testHourlyWeather
        val vm = buildViewModel(name = "Berlin")

        assertEquals("Berlin", vm.state.value.locationName)
    }

    @Test
    fun `LoadData loads rankings for all four activities`() {
        weatherRepository.forecast = testHourlyWeather
        val vm = buildViewModel()
        vm.handleIntent(DetailsIntent.LoadData)

        assertFalse(vm.state.value.isLoading)
        assertFalse(vm.state.value.isError)
        assertEquals(4, vm.state.value.rankings.size)
    }

    @Test
    fun `LoadData sets isError when weather fetch fails`() {
        weatherRepository.shouldFailNetwork = true
        val vm = buildViewModel()
        vm.handleIntent(DetailsIntent.LoadData)

        assertTrue(vm.state.value.isError)
        assertFalse(vm.state.value.isLoading)
    }

    @Test
    fun `LoadData sets isFavorite false when location is not saved`() {
        weatherRepository.forecast = testHourlyWeather
        val vm = buildViewModel()
        vm.handleIntent(DetailsIntent.LoadData)

        assertFalse(vm.state.value.isFavorite)
    }

    @Test
    fun `LoadData sets isFavorite true when location is already saved`() = runTest {
        weatherRepository.forecast = testHourlyWeather
        favoritesRepository.save(berlinFavorite)
        val vm = buildViewModel(lat = BERLIN_LAT, lon = BERLIN_LON)
        vm.handleIntent(DetailsIntent.LoadData)

        assertTrue(vm.state.value.isFavorite)
    }

    @Test
    fun `CancelLoad followed by LoadData reloads rankings`() {
        weatherRepository.forecast = testHourlyWeather
        val vm = buildViewModel()
        vm.handleIntent(DetailsIntent.LoadData)
        assertEquals(4, vm.state.value.rankings.size)

        vm.handleIntent(DetailsIntent.CancelLoad)
        vm.handleIntent(DetailsIntent.LoadData)

        assertEquals(4, vm.state.value.rankings.size)
    }

    @Test
    fun `Refresh reloads rankings`() = runTest {
        weatherRepository.forecast = testHourlyWeather
        val vm = buildViewModel()
        vm.handleIntent(DetailsIntent.LoadData)
        val initialRankings = vm.state.value.rankings

        vm.handleIntent(DetailsIntent.Refresh)

        assertFalse(vm.state.value.isRefreshing)
        assertEquals(initialRankings, vm.state.value.rankings)
    }

    @Test
    fun `ToggleFavorite adds location and sets ADDED_TO_FAVORITES notification`() = runTest {
        weatherRepository.forecast = testHourlyWeather
        val vm = buildViewModel(lat = BERLIN_LAT, lon = BERLIN_LON, name = "Berlin")
        vm.handleIntent(DetailsIntent.LoadData)

        vm.handleIntent(DetailsIntent.ToggleFavorite)

        assertEquals(DetailsMessage.ADDED_TO_FAVORITES, vm.state.value.notification)
        assertTrue(vm.state.value.isFavorite)
    }

    @Test
    fun `ToggleFavorite removes location and sets REMOVED_FROM_FAVORITES notification`() = runTest {
        weatherRepository.forecast = testHourlyWeather
        favoritesRepository.save(berlinFavorite)
        val vm = buildViewModel(lat = BERLIN_LAT, lon = BERLIN_LON, name = "Berlin")
        vm.handleIntent(DetailsIntent.LoadData)

        vm.handleIntent(DetailsIntent.ToggleFavorite)

        assertEquals(DetailsMessage.REMOVED_FROM_FAVORITES, vm.state.value.notification)
        assertFalse(vm.state.value.isFavorite)
    }

    @Test
    fun `DismissNotification clears notification`() = runTest {
        weatherRepository.forecast = testHourlyWeather
        val vm = buildViewModel(lat = BERLIN_LAT, lon = BERLIN_LON)
        vm.handleIntent(DetailsIntent.LoadData)
        vm.handleIntent(DetailsIntent.ToggleFavorite)
        assertNotNull(vm.state.value.notification)

        vm.handleIntent(DetailsIntent.DismissNotification)

        assertNull(vm.state.value.notification)
    }

    private fun buildViewModel(
        lat: Double = BERLIN_LAT,
        lon: Double = BERLIN_LON,
        name: String = "Berlin",
    ): DetailsViewModel {
        return DetailsViewModel(
            latitude = lat,
            longitude = lon,
            locationName = name,
            getRankingUseCase = GetLocationRankingUseCase(
                dispatcher = testDispatcher,
                weatherRepository = weatherRepository,
                getResolvers = GetActivityResolversUseCase(),
            ),
            getFavoriteByCoordinatesUseCase = GetFavoriteByCoordinatesUseCase(favoritesRepository),
            toggleFavoriteUseCase = ToggleFavoriteUseCase(testDispatcher, favoritesRepository),
        )
    }

    companion object {
        private const val BERLIN_LAT = 52.52
        private const val BERLIN_LON = 13.405

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

        private val berlinFavorite = FavoriteLocation(
            id = 1L,
            name = "Berlin",
            country = "Germany",
            latitude = BERLIN_LAT,
            longitude = BERLIN_LON,
            scores = mapOf(
                Activity.SKIING to 10,
                Activity.SURFING to 30,
                Activity.OUTDOOR_SIGHTSEEING to 75,
                Activity.INDOOR_SIGHTSEEING to 20,
            ),
            updatedAt = 1_700_000_000_000L,
        )
    }
}
