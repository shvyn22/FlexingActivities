package shvyn22.flexingactivities.coreui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import shvyn22.flexingactivities.coreui.R
import shvyn22.flexingactivities.coreui.theme.AppTheme

@Composable
fun AppNotificationChip(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.inverseSurface,
    ) {
        Row(
            modifier = Modifier
                .padding(start = 16.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = message,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.inverseOnSurface,
            )
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.action_ok),
                    color = MaterialTheme.colorScheme.inversePrimary,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun AppNotificationChipPreview() {
    AppTheme {
        AppNotificationChip(
            message = "Added to favorites.",
            onDismiss = {},
        )
    }
}