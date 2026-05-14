package com.scelio.brainest.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import brainest.feature.home.presentation.generated.resources.Res
import brainest.feature.home.presentation.generated.resources.home_days_value
import brainest.feature.home.presentation.generated.resources.home_decks_label
import brainest.feature.home.presentation.generated.resources.home_earned_label
import brainest.feature.home.presentation.generated.resources.home_points_value
import brainest.feature.home.presentation.generated.resources.home_quizzes_label
import brainest.feature.home.presentation.generated.resources.home_streak_label
import brainest.feature.home.presentation.generated.resources.ic_bronze
import brainest.feature.home.presentation.generated.resources.ic_lesson
import brainest.feature.home.presentation.generated.resources.ic_vocab
import brainest.feature.home.presentation.generated.resources.ic_yellow_fire
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.components.navbar.home.HomeHeader
import com.scelio.brainest.designsystem.components.vip.vipcard.VipUpgradeCard
import com.scelio.brainest.presentation.home.components.ConsecutiveStudyDaysCard
import com.scelio.brainest.presentation.home.components.HomeHeaderIconsRow
import com.scelio.brainest.presentation.home.components.HomeStatCardUi
import com.scelio.brainest.presentation.home.components.HomeStatsGrid
import com.scelio.brainest.presentation.home.components.StudyDayUi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.loadHome()
        }
    }

    HomeScreen(
        userName = state.userName,
        decks = state.completedDecks,
        quizzes = state.completedQuizzes,
        others = state.completedDaysThisWeek,
        streakDays = state.streakDays,
        earnedPoints = state.earnedPoints,
        studyDays = state.studyDays,
        notificationCount = state.diamondCount,
        modifier = modifier,
        onSettingsClick = onSettingsClick,
        onNotificationsClick = onNotificationsClick
    )
}

@Composable
fun HomeScreen(
    userName: String,
    decks: Int,
    quizzes: Int,
    others: Int,
    streakDays: Int,
    earnedPoints: Int,
    studyDays: List<StudyDayUi>,
    notificationCount: Int = 0,
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
) {
    val cardsTopOffset = 60.dp
    val cardsItemSpacing = 8.dp
    val topNavTouchSafeArea = 96.dp
    val bottomOverlayReserve = 120.dp
    val listState = rememberLazyListState()
    var headerHeightPx by remember { mutableIntStateOf(0) }
    var headerIconsHeightPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val headerHeightDp = with(density) { headerHeightPx.toDp() }
    val headerIconsHeightDp = with(density) { headerIconsHeightPx.toDp() }
    val headerIconsToCardsGap =
        ((headerIconsHeightDp + cardsItemSpacing) * 1.5f) - headerIconsHeightDp
    val listTopContentPadding = (
        headerHeightDp + cardsTopOffset + headerIconsHeightDp + headerIconsToCardsGap - topNavTouchSafeArea
    )
        .let { if (it > 0.dp) it else 0.dp }
    val navigationBottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val stats = listOf(
        HomeStatCardUi(
            value = stringResource(Res.string.home_days_value, streakDays),
            label = stringResource(Res.string.home_streak_label),
            icon = Res.drawable.ic_yellow_fire,
        ),
        HomeStatCardUi(
            value = decks.toString(),
            label = stringResource(Res.string.home_decks_label),
            icon = Res.drawable.ic_lesson,
        ),
        HomeStatCardUi(
            value = quizzes.toString(),
            label = stringResource(Res.string.home_quizzes_label),
            icon = Res.drawable.ic_vocab,
        ),
        HomeStatCardUi(
            value = stringResource(Res.string.home_points_value, earnedPoints),
            label = stringResource(Res.string.home_earned_label),
            icon = Res.drawable.ic_bronze,
        ),
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color(0xFF00AD67))
    ) {
        HomeHeader(
            userName = userName,
            decks = decks,
            quizzes = quizzes,
            others = others,
            notificationCount = notificationCount,
            modifier = Modifier.onSizeChanged { headerHeightPx = it.height },
            onSettingsClick = onSettingsClick,
            onNotificationsClick = onNotificationsClick,
        )

        HomeHeaderIconsRow(
            modifier = Modifier
                .padding(top = headerHeightDp + cardsTopOffset)
                .padding(horizontal = 16.dp)
                .onSizeChanged { headerIconsHeightPx = it.height },
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topNavTouchSafeArea),
            verticalArrangement = Arrangement.spacedBy(cardsItemSpacing),
            contentPadding = PaddingValues(
                top = listTopContentPadding,
                bottom = navigationBottomInset + bottomOverlayReserve
            )
        ) {
            item {
                ConsecutiveStudyDaysCard(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    days = studyDays,
                )
            }
            item {
                HomeStatsGrid(
                    stats = stats,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            item {
                VipUpgradeCard(
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewHomeScreen() {
    BrainestTheme {
        HomeScreen(
            userName = "Student",
            decks = 0,
            quizzes = 0,
            others = 0,
            streakDays = 0,
            earnedPoints = 0,
            studyDays = defaultStudyDays(),
            notificationCount = 1,
        )
    }
}
