package shvyn22.flexingactivities.coreui.components.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import shvyn22.flexingactivities.coreui.R
import shvyn22.flexingactivities.domain.core.model.Activity

@Composable
fun Activity.toDisplayString(): String {
    return stringResource(
        when (this) {
            Activity.SKIING -> R.string.activity_label_skiing
            Activity.SURFING -> R.string.activity_label_surfing
            Activity.OUTDOOR_SIGHTSEEING -> R.string.activity_label_outdoor_sightseeing
            Activity.INDOOR_SIGHTSEEING -> R.string.activity_label_indoor_sightseeing
        }
    )
}

@Composable
fun Activity.toBadgeLabel(): String {
    return stringResource(
        when (this) {
            Activity.SKIING -> R.string.activity_badge_skiing
            Activity.SURFING -> R.string.activity_badge_surfing
            Activity.OUTDOOR_SIGHTSEEING -> R.string.activity_badge_outdoor_sightseeing
            Activity.INDOOR_SIGHTSEEING -> R.string.activity_badge_indoor_sightseeing
        }
    )
}
