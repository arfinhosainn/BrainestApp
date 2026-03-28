package com.scelio.brainest.designsystem.components.navbar.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.scelio.brainest.designsystem.BrainestTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HomeHeader(
    userName: String,
    decks: Int,
    quizzes: Int,
    others: Int,
    notificationCount: Int = 10,
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)) // clip children to bounds
            .background(color = Color(0xFF1B5E3E))
    ) {
        Column {
            HomeTopNavBar(
                userName = userName,
                notificationCount = notificationCount,
                onSettingsClick = onSettingsClick,
                onNotificationsClick = onNotificationsClick,
            )
            Spacer(modifier = Modifier.height(50.dp))

            ProfileStatsOverlay(
                decks = decks,
                quizzes = quizzes,
                others = others,
            )
        }
    }
}
@Preview
@Composable
fun PreviewHomeHeader(){
    BrainestTheme {
        HomeHeader(
            userName = "Wdz996",
            decks = 15,
            quizzes = 490,
            others = 23,
            notificationCount = 10,
            onSettingsClick = { /* navigate to settings */ },
            onNotificationsClick = { /* open notifications */ },
        )
    }
}
