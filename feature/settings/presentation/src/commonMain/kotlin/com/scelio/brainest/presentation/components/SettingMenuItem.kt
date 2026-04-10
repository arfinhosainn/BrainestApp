package com.scelio.brainest.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import brainest.feature.settings.presentation.generated.resources.Res
import brainest.feature.settings.presentation.generated.resources.ic_arrow_right
import brainest.feature.settings.presentation.generated.resources.ic_logout
import brainest.feature.settings.presentation.generated.resources.ic_pen
import com.scelio.brainest.designsystem.BrainestTheme
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SettingMenuItem(
    title: String,
    leadingIcon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showTrailingIcon: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    borderColor: Color = Color.Black,
) {
    val shape = RoundedCornerShape(24.dp)

    Surface(
        color = containerColor,
        shape = shape,
        border = BorderStroke(1.dp, borderColor.copy(0.1f)),
        modifier = modifier
            .fillMaxWidth()
            .height(86.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(28.dp)
            )

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = contentColor,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )

            if (showTrailingIcon) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_arrow_right),
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun SettingMenuItemPreview() {
    BrainestTheme {
        SettingMenuItem(
            title = "Support",
            leadingIcon = vectorResource(Res.drawable.ic_logout),
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun SettingMenuItemDarkPreview() {
    BrainestTheme(darkTheme = true) {
        SettingMenuItem(
            title = "Support",
            leadingIcon = vectorResource(Res.drawable.ic_logout),
            onClick = {},
            containerColor = Color.Black,
            contentColor = MaterialTheme.colorScheme.onSurface,
            borderColor = Color(0xFF1D2A2C)
        )
    }
}
