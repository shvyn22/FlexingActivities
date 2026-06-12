package shvyn22.flexingactivities.domain.weather.resolvers

import kotlin.math.roundToInt
import shvyn22.flexingactivities.domain.core.model.Activity
import shvyn22.flexingactivities.domain.weather.model.ActivityRanking
import shvyn22.flexingactivities.domain.weather.model.DailyScore
import shvyn22.flexingactivities.domain.weather.model.HourlyWeather
import shvyn22.flexingactivities.domain.weather.resolvers.scoring.score
import shvyn22.flexingactivities.domain.weather.resolvers.scoring.toDailyWeatherList

internal class SurfingResolver : ActivityRankingResolver {
    override val activity = Activity.SURFING

    override fun resolve(hourly: HourlyWeather): ActivityRanking {
        val score = hourly.toDailyWeatherList().map { weather ->
            val wind = score(weather.windSpeed10m, 0.0, 2.5, hardHigh = 15.0) * 0.50
            val pressure = score(weather.pressureMsl, 1005.0, 1025.0) * 0.15
            val temp = score(weather.temperature, 20.0, 30.0, hardLow = 0.0, hardHigh = 40.0) * 0.15
            val visibility = score(weather.visibility / 1000.0, 10.0, Double.MAX_VALUE, hardLow = 0.5) * 0.10
            val precipitation = score(weather.precipitation, 0.0, 0.0, hardHigh = 10.0) * 0.10

            DailyScore(
                date = weather.date,
                score = (wind + pressure + temp + visibility + precipitation).roundToInt().coerceIn(0, 100)
            )
        }
        return ActivityRanking(
            activity = activity,
            daily = score,
            overall = score.map { it.score }.average().roundToInt().coerceIn(0, 100)
        )
    }
}