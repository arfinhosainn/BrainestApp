package com.scelio.brainest.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.components.navbar.home.HomeHeader
import com.scelio.brainest.presentation.home.components.ConsecutiveStudyDaysCard
import com.scelio.brainest.presentation.home.components.HomeStatCardUi
import com.scelio.brainest.presentation.home.components.HomeStatsGrid
import com.scelio.brainest.presentation.home.components.StudyDayUi
import org.jetbrains.compose.ui.tooling.preview.Preview
import brainest.feature.home.presentation.generated.resources.Res
import brainest.feature.home.presentation.generated.resources.ic_bronze
import brainest.feature.home.presentation.generated.resources.ic_lesson
import brainest.feature.home.presentation.generated.resources.ic_vocab
import brainest.feature.home.presentation.generated.resources.ic_yellow_fire
import com.scelio.brainest.designsystem.components.vip.vipcard.VipUpgradeCard
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadHome()
    }

    HomeScreen(
        userName = state.userName,
        decks = state.decks,
        quizzes = state.quizzes,
        others = state.others,
        notificationCount = state.notificationCount,
        days = state.days,
        streakDays = state.streakDays,
        modifier = modifier,
        onSettingsClick = onSettingsClick,
        onNotificationsClick = onNotificationsClick,
    )
}

@Composable
fun HomeScreen(
    userName: String,
    decks: Int,
    quizzes: Int,
    others: Int,
    days: List<StudyDayUi>,
    streakDays: Int,
    notificationCount: Int = 0,
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxSize().background(color = Color(0xFFF4F7F6))
            .verticalScroll(rememberScrollState())
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

        ConsecutiveStudyDaysCard(
            modifier = Modifier.padding(horizontal = 16.dp),
            days = days,
        )
        Spacer(modifier = Modifier.height(16.dp))
        HomeStatsGrid(
            stats = listOf(
                HomeStatCardUi(
                    value = "$streakDays Days",
                    label = "Streak",
                    icon = Res.drawable.ic_yellow_fire,
                ),
                HomeStatCardUi(
                    value = "2 / 490",
                    label = "Lessons",
                    icon = Res.drawable.ic_lesson,
                ),
                HomeStatCardUi(
                    value = "3 words",
                    label = "Vocabulary",
                    icon = Res.drawable.ic_vocab,
                ),
                HomeStatCardUi(
                    value = "138 P",
                    label = "Bronze Class",
                    icon = Res.drawable.ic_bronze,
                ),
            ),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        VipUpgradeCard(
            modifier = Modifier.padding(16.dp)
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
            days = buildPreviewWeek(),
            streakDays = 0,
            notificationCount = 1,
        )
    }
}
