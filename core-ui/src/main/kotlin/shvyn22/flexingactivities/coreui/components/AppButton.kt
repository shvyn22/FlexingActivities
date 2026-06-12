package shvyn22.flexingactivities.coreui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import shvyn22.flexingactivities.coreui.theme.AppTheme

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var pressed by remember { mutableStateOf(false) }
    val offsetY by animateDpAsState(
        targetValue = if (pressed) 3.dp else 0.dp,
        label = "pressAnim",
    )

    Button(
        onClick = {
            pressed = true
            onClick()
            pressed = false
        },
        modifier = modifier
            .offset { IntOffset(x = 0, y = offsetY.roundToPx()) },
        enabled = enabled,
        shape = MaterialTheme.shapes.small,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 1.dp,
        ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@PreviewLightDark
@Composable
private fun AppButtonPreview() {
    AppTheme {
        AppButton(text = "SEARCH", onClick = {})
    }
}