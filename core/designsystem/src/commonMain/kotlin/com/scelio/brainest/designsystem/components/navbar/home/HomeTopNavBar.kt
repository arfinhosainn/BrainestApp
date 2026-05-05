package com.scelio.brainest.designsystem.components.navbar.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brainest.core.designsystem.generated.resources.Res
import brainest.core.designsystem.generated.resources.crown
import brainest.core.designsystem.generated.resources.diamonds
import brainest.core.designsystem.generated.resources.ic_diamond
import brainest.core.designsystem.generated.resources.ic_settings
import brainest.core.designsystem.generated.resources.profile_badge
import brainest.core.designsystem.generated.resources.settings
import com.scelio.brainest.designsystem.BrainestTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HomeTopNavBar(
    userName: String,
    modifier: Modifier = Modifier,
    notificationCount: Int = 0,
    badgeIcon: Painter = painterResource(Res.drawable.crown),
    badgeContentDescription: String? = stringResource(Res.string.profile_badge),
    onSettingsClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
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
            DiamondCountChip(
                diamondCount = notificationCount,
                onClick = onNotificationsClick,
            )
            TopNavIconButton(
                imageVector = vectorResource(Res.drawable.ic_settings),
                contentDescription = stringResource(Res.string.settings),
                onClick = onSettingsClick,
            )
        }
    }
}

@Composable
private fun DiamondCountChip(
    diamondCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            modifier = Modifier
                .padding(start = 18.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colorStops = arrayOf(
                            0f to Color.Transparent,
                            0.25f to Color(0xFF141414).copy(alpha = .3f),
                            1f to Color(0xFF141414).copy(alpha = .3f)
                        )
                    )
                )
                .padding(start = 24.dp, end = 14.dp)
                .height(34.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = diamondCount.toString(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                ),
                color = Color.White,
            )
        }

        Icon(
            imageVector = vectorResource(Res.drawable.ic_diamond),
            contentDescription = stringResource(Res.string.diamonds),
            modifier = Modifier
                .size(34.dp)
                .offset(x = (4).dp),
            tint = Color.Unspecified,
        )
    }
}

@Composable
private fun TopNavIconButton(
    imageVector: androidx.compose.ui.graphics.vector.ImageVector,
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
                notificationCount = 12
            )
        }
    }
}
