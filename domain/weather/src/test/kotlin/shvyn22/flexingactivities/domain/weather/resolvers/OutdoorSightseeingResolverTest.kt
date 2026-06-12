package shvyn22.flexingactivities.domain.weather.resolvers

import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import shvyn22.flexingactivities.domain.core.model.Activity

class OutdoorSightseeingResolverTest {

    private lateinit var resolver: OutdoorSightseeingResolver

    @Before
    fun setup() {
        resolver = OutdoorSightseeingResolver()
    }

    @Test
    fun `activity is OUTDOOR_SIGHTSEEING`() {
        assertEquals(Activity.OUTDOOR_SIGHTSEEING, resolver.activity)
    }

    @Test
    fun `ideal outdoor conditions produce score of 100`() {
        val hourly = buildHourlyWeather(
            buildPoint(
                apparentTemperature = 22.0,
                precipitationProbability = 10.0,
                precipitation = 0.0,
                visibility = 20000.0,
                relativeHumidity = 60.0,
                windSpeed10m = 3.0,
                cloudCover = 35.0,
            )
        )

        val ranking = resolver.resolve(hourly)

        assertEquals(100, ranking.daily.first().score)
        assertEquals(100, ranking.overall)
    }

    @Test
    fun `precipitation probability above hardHigh zeroes its sub-score`() {
        val hourly = buildHourlyWeather(
            buildPoint(
                apparentTemperature = 22.0,
                precipitationProbability = 95.0,
                precipitation = 0.0,
                visibility = 20000.0,
                relativeHumidity = 60.0,
                windSpeed10m = 3.0,
                cloudCover = 35.0,
            )
        )

        val ranking = resolver.resolve(hourly)

        // precipProbability sub-score = 0 (*0.25); all others = 100 → 75
        assertEquals(75, ranking.daily.first().score)
    }

    @Test
    fun `freezing apparent temperature zeroes apparent temperature sub-score`() {
        val hourly = buildHourlyWeather(
            buildPoint(
                apparentTemperature = -15.0,
                precipitationProbability = 10.0,
                precipitation = 0.0,
                visibility = 20000.0,
                relativeHumidity = 60.0,
                windSpeed10m = 3.0,
                cloudCover = 35.0,
            )
        )

        val ranking = resolver.resolve(hourly)

        // apparentTemp sub-score = 0 (*0.30); all others = 100 → 70
        assertEquals(70, ranking.daily.first().score)
    }

    @Test
    fun `heavy precipitation zeroes precipitation sub-score`() {
        val hourly = buildHourlyWeather(
            buildPoint(
                apparentTemperature = 22.0,
                precipitationProbability = 10.0,
                precipitation = 15.0,
                visibility = 20000.0,
                relativeHumidity = 60.0,
                windSpeed10m = 3.0,
                cloudCover = 35.0,
            )
        )

        val ranking = resolver.resolve(hourly)

        // precipitation sub-score = 0 (*0.20); all others = 100 → 80
        assertEquals(80, ranking.daily.first().score)
    }

    @Test
    fun `overall is mean of daily scores`() {
        val hourly = buildHourlyWeather(
            buildPoint(date = TEST_DATE, apparentTemperature = 22.0, precipitationProbability = 10.0, precipitation = 0.0, visibility = 20000.0, relativeHumidity = 60.0, windSpeed10m = 3.0, cloudCover = 35.0),
            buildPoint(date = TEST_DATE.plusDays(1), apparentTemperature = 22.0, precipitationProbability = 95.0, precipitation = 0.0, visibility = 20000.0, relativeHumidity = 60.0, windSpeed10m = 3.0, cloudCover = 35.0),
        )

        val ranking = resolver.resolve(hourly)

        assertEquals(2, ranking.daily.size)
        // day1 = 100, day2 = 75 → overall = mean(100, 75) = 87.5 → 88
        assertEquals(88, ranking.overall)
    }
}
