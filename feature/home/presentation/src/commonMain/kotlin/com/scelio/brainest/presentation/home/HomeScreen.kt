package com.scelio.brainest.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.components.navbar.home.HomeHeader
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HomeScreen(
    userName: String,
    decks: Int,
    quizzes: Int,
    others: Int,
    notificationCount: Int = 0,
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        HomeHeader(
            userName = userName,
            decks = decks,
            quizzes = quizzes,
            others = others,
            notificationCount = notificationCount,
            onSettingsClick = onSettingsClick,
            onNotificationsClick = onNotificationsClick,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Welcome back, $userName",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Preview
@Composable
private fun PreviewHomeScreen() {
    BrainestTheme {
        HomeScreen(
            userName = "Student",
            decks = 12,
            quizzes = 4,
            others = 3,
            notificationCount = 1,
        )
    }
}
