package shvyn22.flexingactivities.domain.weather.resolvers

import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import shvyn22.flexingactivities.domain.core.model.Activity

class IndoorSightseeingResolverTest {

    private lateinit var resolver: IndoorSightseeingResolver

    @Before
    fun setup() {
        resolver = IndoorSightseeingResolver()
    }

    @Test
    fun `activity is INDOOR_SIGHTSEEING`() {
        assertEquals(Activity.INDOOR_SIGHTSEEING, resolver.activity)
    }

    @Test
    fun `perfect outdoor conditions produce score near zero`() {
        val hourly = buildHourlyWeather(
            buildPoint(
                temperature = 22.0,
                precipitation = 0.0,
                windSpeed10m = 0.0,
                visibility = 3000.0,
                weatherCode = 0,
            )
        )

        val ranking = resolver.resolve(hourly)

        // All outdoor scores are comfortable → all sub-scores ≈ 0 → indoor score near 0
        assertEquals(0, ranking.daily.first().score)
        assertEquals(0, ranking.overall)
    }

    @Test
    fun `extreme outdoor conditions produce score of 100`() {
        val hourly = buildHourlyWeather(
            buildPoint(
                temperature = -15.0,
                precipitation = 20.0,
                windSpeed10m = 20.0,
                visibility = 0.0,
                weatherCode = 95,
            )
        )

        val ranking = resolver.resolve(hourly)

        // All sub-scores at max: precip=100*0.35, tempDiscomfort=100*0.25, wind=100*0.20, vis=100*0.10, wCode=100*0.10 = 100
        assertEquals(100, ranking.daily.first().score)
        assertEquals(100, ranking.overall)
    }

    @Test
    fun `thunderstorm raises weather code sub-score`() {
        val hourly = buildHourlyWeather(
            buildPoint(
                temperature = 22.0,
                precipitation = 0.0,
                windSpeed10m = 0.0,
                visibility = 3000.0,
                weatherCode = 95,
            )
        )

        val ranking = resolver.resolve(hourly)

        // Only weather code contributes: 100 * 0.10 = 10
        assertEquals(10, ranking.daily.first().score)
    }

    @Test
    fun `fog raises weather code sub-score`() {
        val hourly = buildHourlyWeather(
            buildPoint(
                temperature = 22.0,
                precipitation = 0.0,
                windSpeed10m = 0.0,
                visibility = 3000.0,
                weatherCode = 45,
            )
        )

        val ranking = resolver.resolve(hourly)

        // Only weather code contributes: 70 * 0.10 = 7
        assertEquals(7, ranking.daily.first().score)
    }

    @Test
    fun `heavy rain produces high indoor score`() {
        val hourly = buildHourlyWeather(
            buildPoint(
                temperature = 22.0,
                precipitation = 20.0,
                windSpeed10m = 0.0,
                visibility = 3000.0,
                weatherCode = 0,
            )
        )

        val ranking = resolver.resolve(hourly)

        // Only precipitation contributes fully: 100 * 0.35 = 35
        assertEquals(35, ranking.daily.first().score)
    }

    @Test
    fun `inverted scoring means bad outdoor weather is ranked higher than good outdoor weather`() {
        val goodOutdoor = buildHourlyWeather(
            buildPoint(temperature = 22.0, precipitation = 0.0, windSpeed10m = 0.0, visibility = 3000.0, weatherCode = 0)
        )
        val badOutdoor = buildHourlyWeather(
            buildPoint(temperature = -15.0, precipitation = 20.0, windSpeed10m = 20.0, visibility = 0.0, weatherCode = 95)
        )

        val goodScore = resolver.resolve(goodOutdoor).overall
        val badScore = resolver.resolve(badOutdoor).overall

        assert(badScore > goodScore) {
            "Bad outdoor weather ($badScore) should produce higher indoor score than good outdoor weather ($goodScore)"
        }
    }
}
