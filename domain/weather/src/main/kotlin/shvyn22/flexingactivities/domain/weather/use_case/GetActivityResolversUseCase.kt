package shvyn22.flexingactivities.domain.weather.use_case

import shvyn22.flexingactivities.domain.weather.resolvers.ActivityRankingResolver
import shvyn22.flexingactivities.domain.weather.resolvers.IndoorSightseeingResolver
import shvyn22.flexingactivities.domain.weather.resolvers.OutdoorSightseeingResolver
import shvyn22.flexingactivities.domain.weather.resolvers.SkiingResolver
import shvyn22.flexingactivities.domain.weather.resolvers.SurfingResolver

class GetActivityResolversUseCase {

    operator fun invoke(): List<ActivityRankingResolver> {
        return listOf(
            SkiingResolver(),
            SurfingResolver(),
            OutdoorSightseeingResolver(),
            IndoorSightseeingResolver(),
        )
    }
}