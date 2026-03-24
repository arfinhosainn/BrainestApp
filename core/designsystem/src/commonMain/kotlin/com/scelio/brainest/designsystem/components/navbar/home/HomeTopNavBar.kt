package com.scelio.brainest.designsystem.components.navbar.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brainest.core.designsystem.generated.resources.Res
import brainest.core.designsystem.generated.resources.crown
import brainest.core.designsystem.generated.resources.ic_bell
import brainest.core.designsystem.generated.resources.ic_settings
import brainest.core.designsystem.generated.resources.vip
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.extended
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HomeTopNavBar(
    userName: String,
    modifier: Modifier = Modifier,
    notificationCount: Int = 0,
    badgeIcon: Painter = painterResource(Res.drawable.crown),
    badgeContentDescription: String? = "Profile badge",
    onSettingsClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            CircularProfilePicture()
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = userName,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                )
                Image(
                    painter = badgeIcon,
                    contentDescription = badgeContentDescription,
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TopNavIconButton(
                imageVector = vectorResource(Res.drawable.ic_settings),
                contentDescription = "Settings",
                onClick = onSettingsClick,
            )
            // Bell with badge
            BadgedIconButton(
                imageVector = vectorResource(Res.drawable.ic_bell),
                contentDescription = "Notifications",
                badgeCount = notificationCount,
                onClick = onNotificationsClick,
            )
        }
    }
}

@Composable
private fun BadgedIconButton(
    imageVector: ImageVector,
    contentDescription: String?,
    badgeCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color = Color.White.copy(alpha = 0.3f))
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(bounded = true, radius = 24.dp),
                    onClick = onClick,
                )
                .padding(8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription,
                modifier = Modifier.size(24.dp),
                tint = Color.White,
            )
        }

        // Red badge
        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.TopEnd)
                    .background(Color.Red, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badgeCount.toString(),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 10.sp,
                )
            }
        }
    }
}

@Composable
private fun TopNavIconButton(
    imageVector: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color = Color.White.copy(alpha = 0.3f))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, radius = 24.dp),
                onClick = onClick,
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp),
            tint = tint,
        )
    }
}

@Preview
@Composable
private fun HomeTopNavBarPreview() {
    BrainestTheme {
        Surface(color = Color(0xFF1B5E3E)) {
            HomeTopNavBar(
                userName = "Arfin Hossin",
            )
        }
    }
}
