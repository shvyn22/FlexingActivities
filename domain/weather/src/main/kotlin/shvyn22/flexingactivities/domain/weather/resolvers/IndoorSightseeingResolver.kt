package shvyn22.flexingactivities.domain.weather.resolvers

import kotlin.math.roundToInt
import shvyn22.flexingactivities.domain.core.model.Activity
import shvyn22.flexingactivities.domain.weather.model.ActivityRanking
import shvyn22.flexingactivities.domain.weather.model.DailyScore
import shvyn22.flexingactivities.domain.weather.model.HourlyWeather
import shvyn22.flexingactivities.domain.weather.resolvers.scoring.temperatureDiscomfortScore
import shvyn22.flexingactivities.domain.weather.resolvers.scoring.toDailyWeatherList
import shvyn22.flexingactivities.domain.weather.resolvers.scoring.weatherCodeSeverityScore

internal class IndoorSightseeingResolver : ActivityRankingResolver {
    override val activity = Activity.INDOOR_SIGHTSEEING

    override fun resolve(hourly: HourlyWeather): ActivityRanking {
        val score = hourly.toDailyWeatherList().map { weather ->
            val precipitation = (weather.precipitation / 20.0 * 100).roundToInt().coerceIn(0, 100) * 0.35
            val tempDisc = temperatureDiscomfortScore(weather.temperatureAllDay) * 0.25
            val wind = (weather.windSpeed10mAllDay / 20.0 * 100).roundToInt().coerceIn(0, 100) * 0.20
            val visibility = ((2000.0 - weather.visibility.coerceAtMost(2000.0)) / 2000.0 * 100).roundToInt().coerceIn(0, 100) * 0.10
            val wCode = weatherCodeSeverityScore(weather.weatherCode) * 0.10

            DailyScore(
                weather.date,
                (precipitation + tempDisc + wind + visibility + wCode).roundToInt().coerceIn(0, 100)
            )
        }

        return ActivityRanking(
            activity = activity,
            daily = score,
            overall = score.map { it.score }.average().roundToInt().coerceIn(0, 100)
        )
    }
}