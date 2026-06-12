package shvyn22.flexingactivities.domain.weather.resolvers

import kotlin.math.roundToInt
import shvyn22.flexingactivities.domain.core.model.Activity
import shvyn22.flexingactivities.domain.weather.model.ActivityRanking
import shvyn22.flexingactivities.domain.weather.model.DailyScore
import shvyn22.flexingactivities.domain.weather.model.HourlyWeather
import shvyn22.flexingactivities.domain.weather.resolvers.scoring.cloudCoverScore
import shvyn22.flexingactivities.domain.weather.resolvers.scoring.score
import shvyn22.flexingactivities.domain.weather.resolvers.scoring.toDailyWeatherList

internal class SkiingResolver : ActivityRankingResolver {
    override val activity = Activity.SKIING

    override fun resolve(hourly: HourlyWeather): ActivityRanking {
        val score = hourly.toDailyWeatherList().map { weather ->
            val snowDepth = score(weather.snowDepth, 50.0, Double.MAX_VALUE, hardLow = 10.0) * 0.25
            val snowfall = score(weather.snowfall, 2.0, 15.0, hardLow = 0.0) * 0.20
            val visibility = score(weather.visibility / 1000.0, 10.0, Double.MAX_VALUE, hardLow = 0.5) * 0.15
            val wind = score(weather.windSpeed10m, 0.0, 5.0, hardHigh = 20.0) * 0.15
            val temp = score(weather.temperature, -6.0, 0.0, hardHigh = 10.0) * 0.10
            val rain = score(weather.rain, 0.0, 0.0, hardHigh = 5.0) * 0.10
            val cloud = cloudCoverScore(weather.cloudCover, 20.0, 70.0) * 0.05

            DailyScore(
                date = weather.date,
                score = (snowDepth + snowfall + visibility + wind + temp + rain + cloud).roundToInt().coerceIn(0, 100)
            )
        }

        return ActivityRanking(
            activity = activity,
            daily = score,
            overall = score.map { it.score }.average().roundToInt().coerceIn(0, 100)
        )
    }
}