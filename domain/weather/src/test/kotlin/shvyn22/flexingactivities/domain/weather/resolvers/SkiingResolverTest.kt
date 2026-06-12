package shvyn22.flexingactivities.domain.weather.resolvers

import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import shvyn22.flexingactivities.domain.core.model.Activity
import java.time.LocalDate

class SkiingResolverTest {

    private lateinit var resolver: SkiingResolver

    @Before
    fun setup() {
        resolver = SkiingResolver()
    }

    @Test
    fun `activity is SKIING`() {
        assertEquals(Activity.SKIING, resolver.activity)
    }

    @Test
    fun `ideal skiing conditions produce score of 100`() {
        val hourly = buildHourlyWeather(
            buildPoint(
                snowDepth = 80.0,
                snowfall = 5.0,
                visibility = 20000.0,
                windSpeed10m = 2.0,
                temperature = -3.0,
                rain = 0.0,
                cloudCover = 40.0,
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
                snowDepth = 80.0,
                snowfall = 5.0,
                visibility = 20000.0,
                windSpeed10m = 25.0,
                temperature = -3.0,
                rain = 0.0,
                cloudCover = 40.0,
            )
        )

        val ranking = resolver.resolve(hourly)

        // wind sub-score = 0 (*0.15); all others = 100 → 85
        assertEquals(85, ranking.daily.first().score)
    }

    @Test
    fun `rain above hardHigh zeroes rain sub-score`() {
        val hourly = buildHourlyWeather(
            buildPoint(
                snowDepth = 80.0,
                snowfall = 5.0,
                visibility = 20000.0,
                windSpeed10m = 2.0,
                temperature = -3.0,
                rain = 10.0,
                cloudCover = 40.0,
            )
        )

        val ranking = resolver.resolve(hourly)

        // rain sub-score = 0 (*0.10); all others = 100 → 90
        assertEquals(90, ranking.daily.first().score)
    }

    @Test
    fun `snow depth below hardLow zeroes snowDepth sub-score`() {
        val hourly = buildHourlyWeather(
            buildPoint(
                snowDepth = 5.0,
                snowfall = 5.0,
                visibility = 20000.0,
                windSpeed10m = 2.0,
                temperature = -3.0,
                rain = 0.0,
                cloudCover = 40.0,
            )
        )

        val ranking = resolver.resolve(hourly)

        // snowDepth sub-score = 0 (*0.25); all others = 100 → 75
        assertEquals(75, ranking.daily.first().score)
    }

    @Test
    fun `overall is mean of daily scores`() {
        val day1 = LocalDate.of(2024, 1, 15)
        val day2 = LocalDate.of(2024, 1, 16)

        val hourly = buildHourlyWeather(
            buildPoint(date = day1, snowDepth = 80.0, snowfall = 5.0, windSpeed10m = 2.0, temperature = -3.0, rain = 0.0, cloudCover = 40.0, visibility = 20000.0),
            buildPoint(date = day2, snowDepth = 5.0, snowfall = 5.0, windSpeed10m = 2.0, temperature = -3.0, rain = 0.0, cloudCover = 40.0, visibility = 20000.0),
        )

        val ranking = resolver.resolve(hourly)

        assertEquals(2, ranking.daily.size)
        // day1 = 100, day2 = 75 → overall = mean(100, 75) = 87.5 → 88
        assertEquals(88, ranking.overall)
    }

    @Test
    fun `daily scores are ordered by date`() {
        val day1 = LocalDate.of(2024, 1, 15)
        val day2 = LocalDate.of(2024, 1, 16)

        val hourly = buildHourlyWeather(
            buildPoint(date = day2, snowDepth = 80.0, snowfall = 5.0, windSpeed10m = 2.0, temperature = -3.0),
            buildPoint(date = day1, snowDepth = 80.0, snowfall = 5.0, windSpeed10m = 2.0, temperature = -3.0),
        )

        val ranking = resolver.resolve(hourly)

        assertEquals(day1, ranking.daily[0].date)
        assertEquals(day2, ranking.daily[1].date)
    }
}
