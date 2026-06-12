package shvyn22.flexingactivities.domain.favorites.use_case

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import shvyn22.flexingactivities.domain.core.resource.Resource
import shvyn22.flexingactivities.domain.core.resource.ResourceError
import shvyn22.flexingactivities.domain.favorites.model.testFavorite
import shvyn22.flexingactivities.domain.favorites.repository.FakeFavoritesRepository
import shvyn22.flexingactivities.domain.weather.model.HourlyPoint
import shvyn22.flexingactivities.domain.weather.model.HourlyWeather
import shvyn22.flexingactivities.domain.weather.repository.FakeWeatherRepository
import shvyn22.flexingactivities.domain.weather.use_case.GetActivityResolversUseCase
import shvyn22.flexingactivities.domain.weather.use_case.GetLocationRankingUseCase
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class RefreshFavoriteUseCaseTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var favoritesRepository: FakeFavoritesRepository
    private lateinit var weatherRepository: FakeWeatherRepository
    private lateinit var useCase: RefreshFavoriteUseCase

    @Before
    fun setup() {
        favoritesRepository = FakeFavoritesRepository()
        weatherRepository = FakeWeatherRepository()
        useCase = RefreshFavoriteUseCase(
            testDispatcher,
            favoritesRepository,
            GetLocationRankingUseCase(testDispatcher, weatherRepository, GetActivityResolversUseCase()),
        )
    }

    @Test
    fun `returns Success and updates scores when ranking succeeds`() = runTest {
        weatherRepository.forecast = testHourlyWeather
        favoritesRepository.save(testFavorite)

        val result = useCase(testFavorite.id)

        assertTrue(result is Resource.Success)
        assertTrue(favoritesRepository.getById(testFavorite.id)?.scores?.isNotEmpty() == true)
    }

    @Test
    fun `returns NotFound when location does not exist`() = runTest {
        val result = useCase(999L)

        assertTrue(result is Resource.Error)
        assertEquals(ResourceError.NotFound, (result as Resource.Error).error)
    }

    @Test
    fun `returns NoNetwork when weather fetch fails`() = runTest {
        weatherRepository.shouldFailNetwork = true
        favoritesRepository.save(testFavorite)

        val result = useCase(testFavorite.id)

        assertTrue(result is Resource.Error)
        assertEquals(ResourceError.NoNetwork, (result as Resource.Error).error)
    }

    @Test
    fun `does not modify scores when weather fetch fails`() = runTest {
        val originalScores = testFavorite.scores
        weatherRepository.shouldFailNetwork = true
        favoritesRepository.save(testFavorite)

        useCase(testFavorite.id)

        assertEquals(originalScores, favoritesRepository.getById(testFavorite.id)?.scores)
    }
}

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
