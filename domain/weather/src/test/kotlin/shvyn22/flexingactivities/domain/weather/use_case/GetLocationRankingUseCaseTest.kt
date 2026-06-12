package shvyn22.flexingactivities.domain.weather.use_case

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import shvyn22.flexingactivities.domain.core.model.Activity
import shvyn22.flexingactivities.domain.core.resource.Resource
import shvyn22.flexingactivities.domain.core.resource.ResourceError
import shvyn22.flexingactivities.domain.weather.model.HourlyPoint
import shvyn22.flexingactivities.domain.weather.model.HourlyWeather
import shvyn22.flexingactivities.domain.weather.repository.FakeWeatherRepository
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class GetLocationRankingUseCaseTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeWeatherRepository
    private lateinit var useCase: GetLocationRankingUseCase

    @Before
    fun setup() {
        repository = FakeWeatherRepository()
        useCase = GetLocationRankingUseCase(
            dispatcher = testDispatcher,
            weatherRepository = repository,
            getResolvers = GetActivityResolversUseCase(),
        )
    }

    @Test
    fun `returns rankings for all four activities on success`() = runTest {
        repository.forecast = testHourlyWeather

        val result = useCase(52.52, 13.405)

        assertTrue(result is Resource.Success)
        val rankings = (result as Resource.Success).data.rankings
        assertEquals(4, rankings.size)
        assertTrue(rankings.any { it.activity == Activity.SKIING })
        assertTrue(rankings.any { it.activity == Activity.SURFING })
        assertTrue(rankings.any { it.activity == Activity.OUTDOOR_SIGHTSEEING })
        assertTrue(rankings.any { it.activity == Activity.INDOOR_SIGHTSEEING })
    }

    @Test
    fun `each ranking has a non-empty daily list`() = runTest {
        repository.forecast = testHourlyWeather

        val result = useCase(52.52, 13.405)

        assertTrue(result is Resource.Success)
        (result as Resource.Success).data.rankings.forEach { ranking ->
            assertTrue("${ranking.activity} should have daily scores", ranking.daily.isNotEmpty())
        }
    }

    @Test
    fun `overall score is within 0 to 100 range`() = runTest {
        repository.forecast = testHourlyWeather

        val result = useCase(52.52, 13.405)

        assertTrue(result is Resource.Success)
        (result as Resource.Success).data.rankings.forEach { ranking ->
            assertTrue("${ranking.activity} overall ${ranking.overall} should be >= 0", ranking.overall >= 0)
            assertTrue("${ranking.activity} overall ${ranking.overall} should be <= 100", ranking.overall <= 100)
        }
    }

    @Test
    fun `returns NoNetwork when network fails`() = runTest {
        repository.shouldFailNetwork = true

        val result = useCase(52.52, 13.405)

        assertTrue(result is Resource.Error)
        assertEquals(ResourceError.NoNetwork, (result as Resource.Error).error)
    }

    companion object {
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
