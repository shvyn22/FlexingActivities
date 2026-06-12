package shvyn22.flexingactivities.domain.weather.resolvers

import shvyn22.flexingactivities.domain.core.model.Activity
import shvyn22.flexingactivities.domain.weather.model.ActivityRanking
import shvyn22.flexingactivities.domain.weather.model.HourlyWeather

interface ActivityRankingResolver {
    val activity: Activity
    fun resolve(hourly: HourlyWeather): ActivityRanking
}