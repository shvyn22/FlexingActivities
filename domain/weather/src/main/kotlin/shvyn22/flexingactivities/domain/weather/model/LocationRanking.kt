package shvyn22.flexingactivities.domain.weather.model

import shvyn22.flexingactivities.domain.core.model.Activity
import java.time.LocalDate

data class LocationRanking(
    val rankings: List<ActivityRanking>,
)

data class ActivityRanking(
    val activity: Activity,
    val daily: List<DailyScore>,
    val overall: Int,
)

data class DailyScore(
    val date: LocalDate,
    val score: Int,
)