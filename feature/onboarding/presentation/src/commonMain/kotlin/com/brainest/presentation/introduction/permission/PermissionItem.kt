package com.brainest.presentation.introduction.permission

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scelio.brainest.presentation.permission.PermissionState

@Composable
fun PermissionItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    description: String,
    state: PermissionState = PermissionState.NOT_DETERMINED
) {
    val isGranted = state == PermissionState.GRANTED
    val isDenied = state == PermissionState.DENIED || state == PermissionState.PERMANENTLY_DENIED

    val iconBgColor = when {
        isGranted -> Color(0xFFE8F5E9)
        isDenied  -> Color(0xFFFFEBEE)
        else      -> Color(0xFFF5F0EB)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(color = iconBgColor, shape = RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = state,
                transitionSpec = {
                    (fadeIn(tween(200)) + scaleIn(tween(200), initialScale = 0.8f))
                        .togetherWith(fadeOut(tween(150)))
                }
            ) { targetState ->
                when {
                    targetState == PermissionState.GRANTED -> Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Granted",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(24.dp)
                    )
                    targetState == PermissionState.DENIED ||
                            targetState == PermissionState.PERMANENTLY_DENIED -> Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Denied",
                        tint = Color(0xFFE57373),
                        modifier = Modifier.size(24.dp)
                    )
                    else -> Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = Color(0xFF1A1A1A),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = Color(0xFF1A1A1A)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = when (state) {
                    PermissionState.GRANTED            -> "Permission granted"
                    PermissionState.DENIED             -> "Permission denied"
                    PermissionState.PERMANENTLY_DENIED -> "Permanently denied — open Settings to enable"
                    PermissionState.NOT_DETERMINED     -> description
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                ),
                color = when {
                    isGranted -> Color(0xFF4CAF50)
                    isDenied  -> Color(0xFFE57373)
                    else      -> Color(0xFF666666)
                }
            )
        }
    }
}