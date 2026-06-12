package shvyn22.flexingactivities.domain.weather.resolvers

import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import shvyn22.flexingactivities.domain.core.model.Activity

class SurfingResolverTest {

    private lateinit var resolver: SurfingResolver

    @Before
    fun setup() {
        resolver = SurfingResolver()
    }

    @Test
    fun `activity is SURFING`() {
        assertEquals(Activity.SURFING, resolver.activity)
    }

    @Test
    fun `ideal surfing conditions produce score of 100`() {
        val hourly = buildHourlyWeather(
            buildPoint(
                windSpeed10m = 1.0,
                pressureMsl = 1015.0,
                temperature = 25.0,
                visibility = 20000.0,
                precipitation = 0.0,
            )
        )

        val ranking = resolver.resolve(hourly)

        assertEquals(100, ranking.daily.first().score)
        assertEquals(100, ranking.overall)
    }

    @Test
    fun `wind above hardHigh zeroes wind sub-score`() {
        val hourly = buildHourlyWeather(
            buildPoint(
                windSpeed10m = 20.0,
                pressureMsl = 1015.0,
                temperature = 25.0,
                visibility = 20000.0,
                precipitation = 0.0,
            )
        )

        val ranking = resolver.resolve(hourly)

        // wind sub-score = 0 (*0.50); rest = 100 → 0.15+0.15+0.10+0.10 = 50
        assertEquals(50, ranking.daily.first().score)
    }

    @Test
    fun `precipitation above hardHigh zeroes precipitation sub-score`() {
        val hourly = buildHourlyWeather(
            buildPoint(
                windSpeed10m = 1.0,
                pressureMsl = 1015.0,
                temperature = 25.0,
                visibility = 20000.0,
                precipitation = 15.0,
            )
        )

        val ranking = resolver.resolve(hourly)

        // precipitation sub-score = 0 (*0.10); rest = 100 → 0.50+0.15+0.15+0.10 = 90
        assertEquals(90, ranking.daily.first().score)
    }

    @Test
    fun `freezing temperature zeroes temperature sub-score`() {
        val hourly = buildHourlyWeather(
            buildPoint(
                windSpeed10m = 1.0,
                pressureMsl = 1015.0,
                temperature = -5.0,
                visibility = 20000.0,
                precipitation = 0.0,
            )
        )

        val ranking = resolver.resolve(hourly)

        // temp sub-score = 0 (*0.15); rest = 100 → 0.50+0.15+0.10+0.10 = 85
        assertEquals(85, ranking.daily.first().score)
    }

    @Test
    fun `overall is mean of daily scores`() {
        val hourly = buildHourlyWeather(
            buildPoint(date = TEST_DATE, windSpeed10m = 1.0, pressureMsl = 1015.0, temperature = 25.0, visibility = 20000.0, precipitation = 0.0),
            buildPoint(date = TEST_DATE.plusDays(1), windSpeed10m = 20.0, pressureMsl = 1015.0, temperature = 25.0, visibility = 20000.0, precipitation = 0.0),
        )

        val ranking = resolver.resolve(hourly)

        assertEquals(2, ranking.daily.size)
        // day1 = 100, day2 = 50 → overall = mean(100, 50) = 75
        assertEquals(75, ranking.overall)
    }
}
