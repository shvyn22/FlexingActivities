package shvyn22.flexingactivities.coreui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import shvyn22.flexingactivities.coreui.theme.AppTheme
import shvyn22.flexingactivities.coreui.theme.scoreHigh
import shvyn22.flexingactivities.coreui.theme.scoreLow
import shvyn22.flexingactivities.coreui.theme.scoreMedium
import shvyn22.flexingactivities.coreui.theme.scoreMediumHigh
import shvyn22.flexingactivities.coreui.theme.scoreVeryLow

@Composable
fun ScoreBadge(
    score: Int,
    label: String?,
    modifier: Modifier = Modifier,
) {
    val color = scoreColor(score)
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(color = color, shape = MaterialTheme.shapes.extraSmall)
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Black,
            )
        }
        Text(
            text = "$score",
            style = if (label != null)
                MaterialTheme.typography.labelSmall
            else MaterialTheme.typography.labelMedium,
            color = Color.Black,
        )
    }
}

fun scoreColor(score: Int): Color = when {
    score >= 90 -> scoreHigh
    score >= 75 -> scoreMediumHigh
    score >= 50 -> scoreMedium
    score >= 20 -> scoreLow
    else -> scoreVeryLow
}

@PreviewLightDark
@Composable
private fun ScoreBadgePreview() {
    AppTheme {
        ScoreBadge(score = 75, label = "SKI")
    }
}
