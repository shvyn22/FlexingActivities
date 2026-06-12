package shvyn22.flexingactivities.domain.weather.resolvers

import kotlin.math.roundToInt
import shvyn22.flexingactivities.domain.core.model.Activity
import shvyn22.flexingactivities.domain.weather.model.ActivityRanking
import shvyn22.flexingactivities.domain.weather.model.DailyScore
import shvyn22.flexingactivities.domain.weather.model.HourlyWeather
import shvyn22.flexingactivities.domain.weather.resolvers.scoring.cloudCoverScore
import shvyn22.flexingactivities.domain.weather.resolvers.scoring.score
import shvyn22.flexingactivities.domain.weather.resolvers.scoring.toDailyWeatherList

internal class OutdoorSightseeingResolver : ActivityRankingResolver {
    override val activity = Activity.OUTDOOR_SIGHTSEEING

    override fun resolve(hourly: HourlyWeather): ActivityRanking {
        val score = hourly.toDailyWeatherList().map { weather ->
            val apparentTemperature = score(weather.apparentTemperature, 18.0, 25.0, hardLow = -10.0, hardHigh = 40.0) * 0.30
            val precipitationProbability = score(weather.precipitationProbability, 0.0, 20.0, hardHigh = 90.0) * 0.25
            val precipitation = score(weather.precipitation, 0.0, 0.0, hardHigh = 10.0) * 0.20
            val visibility = score(weather.visibility / 1000.0, 10.0, Double.MAX_VALUE, hardLow = 0.5) * 0.10
            val humidity = score(weather.relativeHumidity, 40.0, 70.0, hardHigh = 95.0) * 0.05
            val wind = score(weather.windSpeed10m, 0.0, 4.0, hardHigh = 20.0) * 0.05
            val cloud = cloudCoverScore(weather.cloudCover, 20.0, 50.0) * 0.05

            DailyScore(
                date = weather.date,
                score = (apparentTemperature + precipitationProbability + precipitation + visibility + humidity + wind + cloud).roundToInt()
                    .coerceIn(0, 100)
            )
        }

        return ActivityRanking(
            activity = activity,
            daily = score,
            overall = score.map { it.score }.average().roundToInt().coerceIn(0, 100)
        )
    }
}