///*
// * Copyright 2026 Kyriakos Georgiopoulos
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//@file:OptIn(ExperimentalSharedTransitionApi::class)
//
//import androidx.compose.animation.AnimatedContent
//import androidx.compose.animation.AnimatedContentTransitionScope
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.AnimatedVisibilityScope
//import androidx.compose.animation.BoundsTransform
//import androidx.compose.animation.ContentTransform
//import androidx.compose.animation.ExperimentalSharedTransitionApi
//import androidx.compose.animation.SharedTransitionLayout
//import androidx.compose.animation.SharedTransitionScope
//import androidx.compose.animation.animateColorAsState
//import androidx.compose.animation.core.FastOutSlowInEasing
//import androidx.compose.animation.core.LinearEasing
//import androidx.compose.animation.core.RepeatMode
//import androidx.compose.animation.core.Spring
//import androidx.compose.animation.core.animateDpAsState
//import androidx.compose.animation.core.animateFloat
//import androidx.compose.animation.core.animateFloatAsState
//import androidx.compose.animation.core.infiniteRepeatable
//import androidx.compose.animation.core.rememberInfiniteTransition
//import androidx.compose.animation.core.spring
//import androidx.compose.animation.core.tween
//import androidx.compose.animation.fadeIn
//import androidx.compose.animation.fadeOut
//import androidx.compose.animation.scaleIn
//import androidx.compose.animation.scaleOut
//import androidx.compose.animation.slideInHorizontally
//import androidx.compose.animation.slideInVertically
//import androidx.compose.animation.slideOutHorizontally
//import androidx.compose.animation.togetherWith
//import androidx.compose.foundation.ScrollState
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
//import androidx.compose.foundation.interaction.MutableInteractionSource
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.itemsIndexed
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.rounded.ArrowBack
//import androidx.compose.material.icons.rounded.Bolt
//import androidx.compose.material.icons.rounded.CheckCircle
//import androidx.compose.material.icons.rounded.ChevronLeft
//import androidx.compose.material.icons.rounded.ChevronRight
//import androidx.compose.material.icons.rounded.DirectionsWalk
//import androidx.compose.material.icons.rounded.Favorite
//import androidx.compose.material.icons.rounded.LocalFireDepartment
//import androidx.compose.material.icons.rounded.Person
//import androidx.compose.material.icons.rounded.Settings
//import androidx.compose.material.icons.rounded.Star
//import androidx.compose.material3.Icon
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.Stable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableIntStateOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.draw.clipToBounds
//import androidx.compose.ui.draw.drawWithCache
//import androidx.compose.ui.draw.shadow
//import androidx.compose.ui.geometry.CornerRadius
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.geometry.Rect
//import androidx.compose.ui.geometry.Size
//import androidx.compose.ui.graphics.BlendMode
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.Path
//import androidx.compose.ui.graphics.PathEffect
//import androidx.compose.ui.graphics.PathMeasure
//import androidx.compose.ui.graphics.StrokeCap
//import androidx.compose.ui.graphics.StrokeJoin
//import androidx.compose.ui.graphics.drawscope.Stroke
//import androidx.compose.ui.graphics.drawscope.clipRect
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.IntOffset
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import java.time.LocalDate
//import java.util.Locale
//import kotlin.math.PI
//import kotlin.math.atan2
//import kotlin.math.cos
//import kotlin.math.max
//import kotlin.math.min
//import kotlin.math.sin
//import kotlin.math.sqrt
//import java.time.format.TextStyle as JavaTextStyle
//
//// --- 1. Core State & Theming ---
//
///**
// * Represents the available destinations within the Health Application.
// */
//enum class HealthAppScreen { Dashboard, Details, Profile, SleepDetails, CalendarDetails, HeartDetails }
//
///**
// * Global color palette and gradient brushes.
// * Marked as @Stable to ensure compose compiler optimization.
// */
//@Stable
//object AppTheme {
//    val Background = Color(0xFFF4F6F8)
//    val Surface = Color(0xFFFFFFFF)
//    val TextPrimary = Color(0xFF111827)
//    val TextSecondary = Color(0xFF6B7280)
//    val Track = Color(0xFFE5E7EB)
//
//    val Steps = Color(0xFF10B981)
//    val Cals = Color(0xFFF59E0B)
//    val Act = Color(0xFF3B82F6)
//
//    val Sleep = Color(0xFF8B5CF6)
//    val SleepDeep = Color(0xFF4C1D95)
//    val SleepRem = Color(0xFF2DD4BF)
//    val SleepLight = Color(0xFF8B5CF6)
//    val SleepAwake = Color(0xFFF97316)
//
//    val Heart = Color(0xFFF43F5E)
//    val Stress = Color(0xFFF59E0B)
//
//    val CalendarGradient = Brush.linearGradient(
//        colors = listOf(Color(0xFF4338CA), Color(0xFF3B82F6)),
//        start = Offset(0f, 0f), end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
//    )
//
//    val TextGradient = Brush.linearGradient(colors = listOf(Color(0xFF1E1B4B), Color(0xFF4338CA)))
//    val VitalityGradient =
//        Brush.linearGradient(colors = listOf(Color(0xFF3B82F6), Color(0xFF10B981)))
//    val SleepGradient = Brush.linearGradient(colors = listOf(Color(0xFF4C1D95), Color(0xFF8B5CF6)))
//    val BPMGradient = Brush.linearGradient(colors = listOf(Color(0xFFE11D48), Color(0xFFFB7185)))
//    val StressGradient = Brush.linearGradient(colors = listOf(Color(0xFFFCD34D), Color(0xFFF59E0B)))
//}
//
//// Cached physical animation specifications to prevent object reallocation
//@OptIn(ExperimentalSharedTransitionApi::class)
//private val ProSpatialTransform =
//    BoundsTransform { _, _ -> spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessLow) }
//private val SpatialSpring = spring<IntOffset>(dampingRatio = 0.8f, stiffness = Spring.StiffnessLow)
//private val ScaleSpring = spring<Float>(dampingRatio = 0.8f, stiffness = Spring.StiffnessLow)
//private val MorphSpring = spring<Float>(dampingRatio = 0.75f, stiffness = Spring.StiffnessLow)
//private val DepthSpring = spring<Float>(dampingRatio = 0.85f, stiffness = Spring.StiffnessLow)
//
//private const val RAD_TO_DEG = 180f / PI.toFloat()
//
//// --- 2. Custom Modifiers ---
//
///**
// * Applies the standard drop shadow and white surface styling used across all dashboard elements.
// */
//fun Modifier.cardStyle() = this
//    .shadow(
//        12.dp,
//        RoundedCornerShape(24.dp),
//        ambientColor = Color(0x03000000),
//        spotColor = Color(0x08000000)
//    )
//    .background(AppTheme.Surface, RoundedCornerShape(24.dp))
//
///**
// * Applies the standard circular shadow and surface styling for interactive buttons.
// */
//fun Modifier.iconButtonStyle() = this
//    .shadow(8.dp, CircleShape, ambientColor = Color(0x05000000), spotColor = Color(0x0A000000))
//    .background(AppTheme.Surface, CircleShape)
//
//
//// --- 3. Root Navigation & Hoisted States ---
//
///**
// * The root entry point of the application.
// * Manages screen state, shared transitions, and hoists global states.
// */
//@Composable
//fun HealthAppRoot() {
//    var currentScreen by remember { mutableStateOf(HealthAppScreen.Dashboard) }
//
//    // HOISTED: Persistent scroll state ensures the dashboard remembers its position.
//    val dashboardScrollState = rememberScrollState()
//
//    val today = remember { LocalDate.now() }
//    val calendarDays = remember {
//        (-180..180).map { offset ->
//            val date = today.plusDays(offset.toLong())
//            val isAchieved = offset <= 0 && date.dayOfMonth % 3 != 0
//            CalendarDay(date, date == today, isAchieved)
//        }
//    }
//    var selectedCalendarIndex by remember { mutableIntStateOf(180) }
//
//    val morphProgressState = animateFloatAsState(
//        targetValue = if (currentScreen == HealthAppScreen.Details) 1f else 0f,
//        animationSpec = MorphSpring,
//        label = "morph"
//    )
//    val morphProgressProvider = remember { { morphProgressState.value } }
//
//    SharedTransitionLayout {
//        AnimatedContent(
//            targetState = currentScreen,
//            label = "app_navigation",
//            transitionSpec = { appTransitionSpec() }) { targetScreen ->
//            when (targetScreen) {
//                HealthAppScreen.Dashboard -> DashboardScreen(
//                    scrollState = dashboardScrollState,
//                    calendarDays = calendarDays,
//                    selectedIndex = selectedCalendarIndex,
//                    onIndexChange = { selectedCalendarIndex = it },
//                    onCalendarClick = { currentScreen = HealthAppScreen.CalendarDetails },
//                    onOrbClick = { currentScreen = HealthAppScreen.Details },
//                    onProfileClick = { currentScreen = HealthAppScreen.Profile },
//                    onSleepClick = { currentScreen = HealthAppScreen.SleepDetails },
//                    onHeartClick = { currentScreen = HealthAppScreen.HeartDetails },
//                    sharedScope = this@SharedTransitionLayout,
//                    animScope = this@AnimatedContent,
//                    morphProgressProvider = morphProgressProvider
//                )
//
//                HealthAppScreen.Details -> DetailsScreen(
//                    onBackClick = { currentScreen = HealthAppScreen.Dashboard },
//                    sharedScope = this@SharedTransitionLayout,
//                    animScope = this@AnimatedContent,
//                    morphProgressProvider = morphProgressProvider
//                )
//
//                HealthAppScreen.Profile -> ProfileScreen(onBackClick = {
//                    currentScreen = HealthAppScreen.Dashboard
//                }, sharedScope = this@SharedTransitionLayout, animScope = this@AnimatedContent)
//
//                HealthAppScreen.SleepDetails -> SleepDetailsScreen(onBackClick = {
//                    currentScreen = HealthAppScreen.Dashboard
//                }, sharedScope = this@SharedTransitionLayout, animScope = this@AnimatedContent)
//
//                HealthAppScreen.CalendarDetails -> CalendarDetailsScreen(
//                    selectedDate = calendarDays[selectedCalendarIndex].date,
//                    onBackClick = { currentScreen = HealthAppScreen.Dashboard },
//                    sharedScope = this@SharedTransitionLayout,
//                    animScope = this@AnimatedContent
//                )
//
//                HealthAppScreen.HeartDetails -> HeartDetailsScreen(onBackClick = {
//                    currentScreen = HealthAppScreen.Dashboard
//                }, sharedScope = this@SharedTransitionLayout, animScope = this@AnimatedContent)
//            }
//        }
//    }
//}
//
///**
// * Custom spatial physics determining how screens enter and exit the view.
// */
//private fun AnimatedContentTransitionScope<HealthAppScreen>.appTransitionSpec(): ContentTransform {
//    val isProfileTransition =
//        targetState == HealthAppScreen.Profile || initialState == HealthAppScreen.Profile
//    return if (isProfileTransition) {
//        val slideLeft = targetState == HealthAppScreen.Profile
//        val enter = slideInHorizontally(
//            animationSpec = SpatialSpring,
//            initialOffsetX = { if (slideLeft) it else -it }) + fadeIn(tween(400))
//        val exit = slideOutHorizontally(
//            animationSpec = SpatialSpring,
//            targetOffsetX = { if (slideLeft) -it else it }) + scaleOut(
//            targetScale = 0.9f,
//            animationSpec = ScaleSpring
//        ) + fadeOut(tween(400))
//        enter togetherWith exit
//    } else {
//        val enter = fadeIn(tween(400, easing = FastOutSlowInEasing)) + scaleIn(
//            initialScale = 0.92f,
//            animationSpec = DepthSpring
//        )
//        val exit = fadeOut(tween(300, easing = FastOutSlowInEasing)) + scaleOut(
//            targetScale = 0.92f,
//            animationSpec = DepthSpring
//        )
//        enter togetherWith exit
//    }
//}
//
//// --- 4. Shared Header Components ---
//
//@Composable
//fun DetailTopBar(title: String, onBackClick: () -> Unit) {
//    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
//        Box(
//            modifier = Modifier
//                .size(48.dp)
//                .iconButtonStyle()
//                .clickable { onBackClick() },
//            contentAlignment = Alignment.Center
//        ) {
//            Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = AppTheme.TextPrimary)
//        }
//        Spacer(modifier = Modifier.weight(1f))
//        Text(title, color = AppTheme.TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
//        Spacer(modifier = Modifier.weight(1f))
//        Spacer(modifier = Modifier.size(48.dp))
//    }
//}
//
//@Composable
//fun HeaderSection(
//    onProfileClick: () -> Unit,
//    sharedScope: SharedTransitionScope,
//    animScope: AnimatedVisibilityScope
//) {
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Column {
//            Text(
//                "Morning, Kyriakos",
//                style = TextStyle(brush = AppTheme.TextGradient),
//                fontSize = 32.sp,
//                fontWeight = FontWeight.ExtraBold,
//                letterSpacing = (-0.5).sp
//            )
//            Text(
//                "March 10, 2026",
//                color = AppTheme.TextSecondary,
//                fontSize = 16.sp,
//                fontWeight = FontWeight.Medium
//            )
//        }
//        with(sharedScope) {
//            Box(
//                modifier = Modifier
//                    .sharedElement(
//                        rememberSharedContentState("profile_avatar"),
//                        animScope
//                    )
//                    .size(56.dp)
//                    .iconButtonStyle()
//                    .clickable(
//                        remember { MutableInteractionSource() },
//                        null,
//                        onClick = onProfileClick
//                    ),
//                contentAlignment = Alignment.Center
//            ) {
//                Box(
//                    modifier = Modifier
//                        .size(44.dp)
//                        .background(AppTheme.Track, CircleShape),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        Icons.Rounded.Person,
//                        contentDescription = "Profile",
//                        tint = AppTheme.TextSecondary,
//                        modifier = Modifier.size(24.dp)
//                    )
//                }
//            }
//        }
//    }
//}
//
//// --- 5. Main Screens ---
//
///**
// * The central dashboard serving as the hub for all daily biometric data.
// */
//@Composable
//fun DashboardScreen(
//    scrollState: ScrollState,
//    calendarDays: List<CalendarDay>,
//    selectedIndex: Int,
//    onIndexChange: (Int) -> Unit,
//    onCalendarClick: () -> Unit,
//    onOrbClick: () -> Unit,
//    onProfileClick: () -> Unit,
//    onSleepClick: () -> Unit,
//    onHeartClick: () -> Unit,
//    sharedScope: SharedTransitionScope,
//    animScope: AnimatedVisibilityScope,
//    morphProgressProvider: () -> Float
//) {
//    Box(modifier = Modifier
//        .fillMaxSize()
//        .background(AppTheme.Background)) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .verticalScroll(scrollState)
//                .padding(horizontal = 24.dp)
//        ) {
//            Spacer(modifier = Modifier.height(64.dp))
//            HeaderSection(onProfileClick, sharedScope, animScope)
//
//            Spacer(modifier = Modifier.height(40.dp))
//            Text(
//                "Your Journey",
//                color = AppTheme.TextPrimary,
//                fontSize = 20.sp,
//                fontWeight = FontWeight.Bold
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//            GoalCalendar(
//                calendarDays,
//                selectedIndex,
//                onIndexChange,
//                onCalendarClick,
//                sharedScope,
//                animScope
//            )
//
//            Spacer(modifier = Modifier.height(32.dp))
//            Text(
//                "Today's Overview",
//                color = AppTheme.TextPrimary,
//                fontSize = 20.sp,
//                fontWeight = FontWeight.Bold
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Vitality Card
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .cardStyle()
//                    .clickable(remember { MutableInteractionSource() }, null, onClick = onOrbClick)
//                    .padding(24.dp)
//            ) {
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    with(sharedScope) {
//                        BioOrbCanvas(
//                            modifier = Modifier
//                                .size(140.dp)
//                                .sharedElement(
//                                    rememberSharedContentState("vitality_orb"),
//                                    animScope
//                                ),
//                            stepsProgress = 0.8f,
//                            calsProgress = 0.65f,
//                            actProgress = 0.9f,
//                            morphProgressProvider = morphProgressProvider
//                        )
//                    }
//                    Spacer(modifier = Modifier.width(24.dp))
//                    Column(modifier = Modifier.weight(1f)) {
//                        with(sharedScope) {
//                            Text(
//                                "VITALITY",
//                                color = AppTheme.TextSecondary,
//                                fontSize = 12.sp,
//                                fontWeight = FontWeight.Bold,
//                                letterSpacing = 2.sp,
//                                modifier = Modifier.sharedBounds(
//                                    rememberSharedContentState("vitality_label"),
//                                    animScope,
//                                    resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds()
//                                )
//                            )
//                            Text(
//                                "88",
//                                style = TextStyle(brush = AppTheme.VitalityGradient),
//                                fontSize = 48.sp,
//                                fontWeight = FontWeight.Black,
//                                modifier = Modifier.sharedBounds(
//                                    rememberSharedContentState("vitality_number"),
//                                    animScope,
//                                    resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds()
//                                )
//                            )
//                        }
//                        Text(
//                            "Excellent • +2 from yesterday",
//                            color = AppTheme.Steps,
//                            fontSize = 11.sp,
//                            fontWeight = FontWeight.Bold
//                        )
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//            SleepScoreCard(onSleepClick, sharedScope, animScope)
//            Spacer(modifier = Modifier.height(16.dp))
//            BpmScoreCard(onHeartClick, sharedScope, animScope)
//            Spacer(modifier = Modifier.height(16.dp))
//            StressScoreCard(sharedScope, animScope)
//            Spacer(modifier = Modifier.height(48.dp))
//        }
//    }
//}
//
//// --- 6. Calendar Details & Graphs ---
//
//@Stable
//data class CalendarDay(val date: LocalDate, val isToday: Boolean, val goalAchieved: Boolean)
//
//@Composable
//fun GoalCalendar(
//    calendarDays: List<CalendarDay>,
//    selectedIndex: Int,
//    onIndexChange: (Int) -> Unit,
//    onCalendarClick: () -> Unit,
//    sharedScope: SharedTransitionScope,
//    animScope: AnimatedVisibilityScope
//) {
//    val coroutineScope = rememberCoroutineScope()
//    val listState = rememberLazyListState(initialFirstVisibleItemIndex = max(0, selectedIndex - 2))
//    val selectedDate = calendarDays[selectedIndex].date
//    val monthName = selectedDate.month.getDisplayName(JavaTextStyle.FULL, Locale.getDefault())
//    val yearName = selectedDate.year
//
//    Box(modifier = Modifier
//        .fillMaxWidth()
//        .cardStyle()
//        .padding(vertical = 24.dp)) {
//        Column(modifier = Modifier.fillMaxWidth()) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 24.dp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Icon(
//                    Icons.Rounded.ChevronLeft,
//                    contentDescription = "Previous",
//                    modifier = Modifier
//                        .size(28.dp)
//                        .clip(CircleShape)
//                        .clickable {
//                            val targetDate = selectedDate.minusMonths(1).withDayOfMonth(1)
//                            val targetIndex = calendarDays.indexOfFirst { it.date == targetDate }
//                            if (targetIndex != -1) {
//                                onIndexChange(targetIndex); coroutineScope.launch {
//                                    listState.animateScrollToItem(
//                                        max(0, targetIndex - 2)
//                                    )
//                                }
//                            }
//                        }
//                        .graphicsLayer(alpha = 0.99f)
//                        .drawWithCache {
//                            onDrawWithContent {
//                                drawContent(); drawRect(
//                                AppTheme.CalendarGradient,
//                                blendMode = BlendMode.SrcAtop
//                            )
//                            }
//                        }
//                )
//                Text(
//                    "$monthName $yearName",
//                    color = AppTheme.TextPrimary,
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Bold
//                )
//                Icon(
//                    Icons.Rounded.ChevronRight,
//                    contentDescription = "Next",
//                    modifier = Modifier
//                        .size(28.dp)
//                        .clip(CircleShape)
//                        .clickable {
//                            val targetDate = selectedDate.plusMonths(1).withDayOfMonth(1)
//                            val targetIndex = calendarDays.indexOfFirst { it.date == targetDate }
//                            if (targetIndex != -1) {
//                                onIndexChange(targetIndex); coroutineScope.launch {
//                                    listState.animateScrollToItem(
//                                        max(0, targetIndex - 2)
//                                    )
//                                }
//                            }
//                        }
//                        .graphicsLayer(alpha = 0.99f)
//                        .drawWithCache {
//                            onDrawWithContent {
//                                drawContent(); drawRect(
//                                AppTheme.CalendarGradient,
//                                blendMode = BlendMode.SrcAtop
//                            )
//                            }
//                        }
//                )
//            }
//            Spacer(modifier = Modifier.height(20.dp))
//            LazyRow(
//                state = listState,
//                contentPadding = PaddingValues(horizontal = 24.dp),
//                horizontalArrangement = Arrangement.spacedBy(12.dp),
//                verticalAlignment = Alignment.CenterVertically,
//                flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(130.dp)
//            ) {
//                itemsIndexed(
//                    items = calendarDays,
//                    key = { _, day -> day.date.toEpochDay() }) { index, dayInfo ->
//                    val isSelected = selectedIndex == index
//                    val dayOfWeek = dayInfo.date.dayOfWeek.getDisplayName(
//                        JavaTextStyle.SHORT,
//                        Locale.getDefault()
//                    )
//                    val dayOfMonth = dayInfo.date.dayOfMonth.toString().padStart(2, '0')
//                    val monthShort =
//                        dayInfo.date.month.getDisplayName(JavaTextStyle.SHORT, Locale.getDefault())
//
//                    val cardSpring = spring<androidx.compose.ui.unit.Dp>(
//                        dampingRatio = 0.6f,
//                        stiffness = Spring.StiffnessLow
//                    )
//                    val width by animateDpAsState(
//                        if (isSelected) 84.dp else 68.dp,
//                        cardSpring,
//                        label = "width"
//                    )
//                    val height by animateDpAsState(
//                        if (isSelected) 124.dp else 108.dp,
//                        cardSpring,
//                        label = "height"
//                    )
//
//                    val selectionAlpha by animateFloatAsState(
//                        if (isSelected) 1f else 0f,
//                        tween(300),
//                        label = "alpha"
//                    )
//                    val primaryTextColor by animateColorAsState(
//                        if (isSelected) AppTheme.Surface else AppTheme.TextPrimary,
//                        tween(300),
//                        label = "text1"
//                    )
//                    val secondaryTextColor by animateColorAsState(
//                        if (isSelected) AppTheme.Surface.copy(
//                            alpha = 0.8f
//                        ) else AppTheme.TextSecondary, tween(300), label = "text2"
//                    )
//                    val checkColor by animateColorAsState(
//                        if (isSelected) AppTheme.Surface else AppTheme.Steps,
//                        tween(300),
//                        label = "check"
//                    )
//                    val unselectedBg = Color(0xFFF8FAFC)
//
//                    Box(
//                        modifier = Modifier
//                            .width(width)
//                            .height(height)
//                            .then(
//                                if (isSelected) {
//                                    with(sharedScope) {
//                                        Modifier.sharedBounds(
//                                            rememberSharedContentState(
//                                                "calendar_card_bg"
//                                            ),
//                                            animScope,
//                                            resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
//                                            boundsTransform = ProSpatialTransform
//                                        )
//                                    }
//                                } else Modifier)
//                            .shadow(
//                                if (isSelected) 16.dp else 0.dp,
//                                RoundedCornerShape(24.dp),
//                                ambientColor = Color(0x05000000),
//                                spotColor = Color(0x0A000000)
//                            )
//                            .background(unselectedBg, RoundedCornerShape(24.dp))
//                            .border(
//                                1.dp,
//                                Brush.linearGradient(
//                                    colors = listOf(
//                                        Color(0xFF4338CA).copy(alpha = 1f - selectionAlpha),
//                                        Color(0xFF3B82F6).copy(alpha = 1f - selectionAlpha)
//                                    )
//                                ),
//                                RoundedCornerShape(24.dp)
//                            )
//                            .clip(RoundedCornerShape(24.dp))
//                            .clickable(
//                                remember { MutableInteractionSource() },
//                                null
//                            ) { if (isSelected) onCalendarClick() else onIndexChange(index) }
//                    ) {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .graphicsLayer { alpha = selectionAlpha }
//                                .background(AppTheme.CalendarGradient)
//                        )
//                        Box(
//                            modifier = Modifier.fillMaxSize(),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Column(
//                                horizontalAlignment = Alignment.CenterHorizontally,
//                                verticalArrangement = Arrangement.Center
//                            ) {
//                                val dayMod = if (isSelected) with(sharedScope) {
//                                    Modifier.sharedBounds(
//                                        rememberSharedContentState("cal_day"),
//                                        animScope,
//                                        resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
//                                        boundsTransform = ProSpatialTransform
//                                    )
//                                } else Modifier
//                                val dateMod = if (isSelected) with(sharedScope) {
//                                    Modifier.sharedBounds(
//                                        rememberSharedContentState("cal_date"),
//                                        animScope,
//                                        resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
//                                        boundsTransform = ProSpatialTransform
//                                    )
//                                } else Modifier
//                                val monthMod = if (isSelected) with(sharedScope) {
//                                    Modifier.sharedBounds(
//                                        rememberSharedContentState("cal_month"),
//                                        animScope,
//                                        resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
//                                        boundsTransform = ProSpatialTransform
//                                    )
//                                } else Modifier
//
//                                Text(
//                                    dayOfWeek,
//                                    color = secondaryTextColor,
//                                    fontSize = 13.sp,
//                                    fontWeight = FontWeight.SemiBold,
//                                    modifier = dayMod
//                                )
//                                Spacer(modifier = Modifier.height(2.dp))
//                                Text(
//                                    dayOfMonth,
//                                    color = primaryTextColor,
//                                    fontSize = 34.sp,
//                                    fontWeight = FontWeight.Black,
//                                    modifier = dateMod
//                                )
//                                Spacer(modifier = Modifier.height(2.dp))
//                                Text(
//                                    if (dayInfo.isToday) "today" else monthShort,
//                                    color = secondaryTextColor,
//                                    fontSize = 11.sp,
//                                    fontWeight = FontWeight.Bold,
//                                    modifier = monthMod
//                                )
//                                Spacer(modifier = Modifier.height(6.dp))
//                                if (dayInfo.goalAchieved) Icon(
//                                    Icons.Rounded.CheckCircle,
//                                    contentDescription = "Achieved",
//                                    tint = checkColor,
//                                    modifier = Modifier.size(16.dp)
//                                )
//                                else Spacer(modifier = Modifier.height(16.dp))
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun CalendarDetailsScreen(
//    selectedDate: LocalDate,
//    onBackClick: () -> Unit,
//    sharedScope: SharedTransitionScope,
//    animScope: AnimatedVisibilityScope
//) {
//    var isLoaded by remember { mutableStateOf(false) }
//    LaunchedEffect(Unit) { delay(50); isLoaded = true }
//
//    val dayString = remember(selectedDate) {
//        selectedDate.dayOfWeek.getDisplayName(
//            JavaTextStyle.FULL,
//            Locale.getDefault()
//        ) + ","
//    }
//    val dateString = remember(selectedDate) { selectedDate.dayOfMonth.toString() }
//    val monthString = remember(selectedDate) {
//        selectedDate.month.getDisplayName(
//            JavaTextStyle.FULL,
//            Locale.getDefault()
//        )
//    }
//
//    Box(modifier = Modifier
//        .fillMaxSize()
//        .background(AppTheme.Background)) {
//        Column(modifier = Modifier
//            .fillMaxSize()
//            .verticalScroll(rememberScrollState())) {
//            Spacer(modifier = Modifier.height(56.dp))
//            Box(modifier = Modifier.padding(horizontal = 24.dp)) {
//                DetailTopBar(
//                    "Daily Overview",
//                    onBackClick
//                )
//            }
//            Spacer(modifier = Modifier.height(24.dp))
//
//            with(sharedScope) {
//                Box(
//                    modifier = Modifier
//                        .padding(horizontal = 24.dp)
//                        .fillMaxWidth()
//                        .height(100.dp)
//                        .shadow(16.dp, RoundedCornerShape(24.dp), ambientColor = Color(0x0A000000))
//                        .sharedBounds(
//                            rememberSharedContentState("calendar_card_bg"),
//                            animScope,
//                            resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
//                            boundsTransform = ProSpatialTransform
//                        )
//                        .background(AppTheme.CalendarGradient, RoundedCornerShape(24.dp)),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Row(verticalAlignment = Alignment.Bottom) {
//                        Text(
//                            text = dayString,
//                            color = AppTheme.Surface,
//                            fontSize = 20.sp,
//                            fontWeight = FontWeight.Bold,
//                            modifier = Modifier.sharedBounds(
//                                rememberSharedContentState("cal_day"),
//                                animScope,
//                                resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
//                                boundsTransform = ProSpatialTransform
//                            )
//                        )
//                        Spacer(modifier = Modifier.width(6.dp))
//                        Text(
//                            text = dateString,
//                            color = AppTheme.Surface,
//                            fontSize = 24.sp,
//                            fontWeight = FontWeight.Black,
//                            modifier = Modifier.sharedBounds(
//                                rememberSharedContentState("cal_date"),
//                                animScope,
//                                resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
//                                boundsTransform = ProSpatialTransform
//                            )
//                        )
//                        Spacer(modifier = Modifier.width(6.dp))
//                        Text(
//                            text = monthString,
//                            color = AppTheme.Surface,
//                            fontSize = 20.sp,
//                            fontWeight = FontWeight.Bold,
//                            modifier = Modifier.sharedBounds(
//                                rememberSharedContentState("cal_month"),
//                                animScope,
//                                resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
//                                boundsTransform = ProSpatialTransform
//                            )
//                        )
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.height(32.dp))
//            val cascadingSpring =
//                spring<IntOffset>(dampingRatio = 0.7f, stiffness = Spring.StiffnessLow)
//
//            AnimatedVisibility(
//                visible = isLoaded,
//                enter = fadeIn() + slideInVertically(animationSpec = cascadingSpring) { 150 }) {
//                SimpleWeekCalendar(
//                    selectedDate
//                )
//            }
//            Spacer(modifier = Modifier.height(32.dp))
//            AnimatedVisibility(
//                visible = isLoaded,
//                enter = fadeIn() + slideInVertically(animationSpec = cascadingSpring) { 250 }) {
//                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
//                    AnimatedBarGraph(
//                        "Steps over Time",
//                        AppTheme.Steps,
//                        listOf(1200f, 3400f, 800f, 4500f, 2100f, 400f)
//                    )
//                    Spacer(modifier = Modifier.height(16.dp))
//                    AnimatedLineGraph(
//                        "Active Time (mins)",
//                        AppTheme.Act,
//                        listOf(10f, 45f, 15f, 60f, 25f, 5f)
//                    )
//                    Spacer(modifier = Modifier.height(16.dp))
//                    AnimatedBarGraph(
//                        "Activity Calories",
//                        AppTheme.Cals,
//                        listOf(150f, 400f, 100f, 550f, 250f, 50f)
//                    )
//                    Spacer(modifier = Modifier.height(48.dp))
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun SimpleWeekCalendar(selectedDate: LocalDate) {
//    val daysToSubtract = if (selectedDate.dayOfWeek.value == 7) 0 else selectedDate.dayOfWeek.value
//    val startOfWeek = selectedDate.minusDays(daysToSubtract.toLong())
//
//    Box(modifier = Modifier
//        .fillMaxWidth()
//        .background(AppTheme.Surface)
//        .padding(vertical = 16.dp)) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            for (i in 0..6) {
//                val date = startOfWeek.plusDays(i.toLong())
//                val isSelected = date == selectedDate
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    modifier = Modifier
//                        .clip(RoundedCornerShape(32.dp))
//                        .background(if (isSelected) AppTheme.Steps else Color.Transparent)
//                        .padding(horizontal = 14.dp, vertical = 16.dp)
//                ) {
//                    Text(
//                        text = date.dayOfWeek.getDisplayName(
//                            JavaTextStyle.SHORT,
//                            Locale.getDefault()
//                        ),
//                        color = if (isSelected) AppTheme.Surface else AppTheme.TextSecondary,
//                        fontSize = 13.sp,
//                        fontWeight = FontWeight.Medium
//                    )
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Text(
//                        text = date.dayOfMonth.toString(),
//                        color = if (isSelected) AppTheme.Surface else AppTheme.TextPrimary,
//                        fontSize = 18.sp,
//                        fontWeight = FontWeight.Bold
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun AnimatedBarGraph(title: String, color: Color, data: List<Float>) {
//    var loaded by remember { mutableStateOf(false) }
//    LaunchedEffect(Unit) { delay(300); loaded = true }
//    val progress by animateFloatAsState(
//        if (loaded) 1f else 0f,
//        tween(1000, easing = FastOutSlowInEasing),
//        label = "bar"
//    )
//
//    Box(modifier = Modifier
//        .fillMaxWidth()
//        .cardStyle()
//        .padding(24.dp)) {
//        Column {
//            Text(
//                title,
//                color = AppTheme.TextPrimary,
//                fontSize = 16.sp,
//                fontWeight = FontWeight.Bold
//            )
//            Spacer(modifier = Modifier.height(24.dp))
//            androidx.compose.foundation.Canvas(modifier = Modifier
//                .fillMaxWidth()
//                .height(120.dp)) {
//                val maxData = data.maxOrNull() ?: 1f
//                val spacing = 12.dp.toPx()
//                val barWidth = (size.width - spacing * (data.size - 1)) / data.size
//                for (i in 0..2) {
//                    val y = size.height * (i / 2f)
//                    drawLine(AppTheme.Track, Offset(0f, y), Offset(size.width, y), 1.dp.toPx())
//                }
//                data.forEachIndexed { i, value ->
//                    val targetH = (value / maxData) * size.height
//                    val currentH = targetH * progress
//                    val topLeft = Offset(i * (barWidth + spacing), size.height - currentH)
//                    drawRoundRect(
//                        color = color,
//                        topLeft = topLeft,
//                        size = Size(barWidth, currentH),
//                        cornerRadius = CornerRadius(6.dp.toPx())
//                    )
//                }
//            }
//            Spacer(modifier = Modifier.height(12.dp))
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                listOf("6am", "10am", "2pm", "6pm", "10pm", "2am").forEach {
//                    Text(
//                        it,
//                        color = AppTheme.TextSecondary,
//                        fontSize = 10.sp,
//                        fontWeight = FontWeight.Medium
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun AnimatedLineGraph(title: String, color: Color, data: List<Float>) {
//    var loaded by remember { mutableStateOf(false) }
//    LaunchedEffect(Unit) { delay(400); loaded = true }
//    val progress by animateFloatAsState(
//        if (loaded) 1f else 0f,
//        tween(1500, easing = FastOutSlowInEasing),
//        label = "line"
//    )
//
//    val path = remember { Path() }
//    val fillPath = remember { Path() }
//    val pathMeasure = remember { PathMeasure() }
//
//    Box(modifier = Modifier
//        .fillMaxWidth()
//        .cardStyle()
//        .padding(24.dp)) {
//        Column {
//            Text(
//                title,
//                color = AppTheme.TextPrimary,
//                fontSize = 16.sp,
//                fontWeight = FontWeight.Bold
//            )
//            Spacer(modifier = Modifier.height(24.dp))
//            androidx.compose.foundation.Canvas(modifier = Modifier
//                .fillMaxWidth()
//                .height(120.dp)) {
//                val maxData = data.maxOrNull() ?: 1f
//                val stepX = size.width / max(1, data.size - 1)
//                for (i in 0..2) {
//                    val y = size.height * (i / 2f)
//                    drawLine(AppTheme.Track, Offset(0f, y), Offset(size.width, y), 1.dp.toPx())
//                }
//                path.rewind()
//                val points = data.mapIndexed { i, value ->
//                    Offset(
//                        i * stepX,
//                        size.height - ((value / maxData) * size.height)
//                    )
//                }
//                path.moveTo(points[0].x, points[0].y)
//                for (i in 1 until points.size) {
//                    val p1 = points[i - 1];
//                    val p2 = points[i]
//                    path.cubicTo(
//                        p1.x + (p2.x - p1.x) / 2f,
//                        p1.y,
//                        p1.x + (p2.x - p1.x) / 2f,
//                        p2.y,
//                        p2.x,
//                        p2.y
//                    )
//                }
//                pathMeasure.setPath(path, false)
//                clipRect(right = size.width * progress) {
//                    fillPath.rewind()
//                    fillPath.addPath(path)
//                    fillPath.lineTo(points.last().x, size.height)
//                    fillPath.lineTo(points.first().x, size.height)
//                    fillPath.close()
//                    drawPath(
//                        fillPath,
//                        brush = Brush.verticalGradient(
//                            listOf(
//                                color.copy(alpha = 0.3f),
//                                Color.Transparent
//                            )
//                        )
//                    )
//                    drawPath(
//                        path,
//                        color,
//                        style = Stroke(4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
//                    )
//                }
//                points.forEach { pt ->
//                    if (pt.x <= size.width * progress) {
//                        drawCircle(AppTheme.Surface, 6.dp.toPx(), pt); drawCircle(
//                            color,
//                            4.dp.toPx(),
//                            pt
//                        )
//                    }
//                }
//            }
//            Spacer(modifier = Modifier.height(12.dp))
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                listOf("6am", "10am", "2pm", "6pm", "10pm", "2am").forEach {
//                    Text(
//                        it,
//                        color = AppTheme.TextSecondary,
//                        fontSize = 10.sp,
//                        fontWeight = FontWeight.Medium
//                    )
//                }
//            }
//        }
//    }
//}
//
//// --- 7. Remaining Components (Heart, Stress, Sleep, etc) ---
//
//@Composable
//fun HeartDetailsScreen(
//    onBackClick: () -> Unit,
//    sharedScope: SharedTransitionScope,
//    animScope: AnimatedVisibilityScope
//) {
//    var isLoaded by remember { mutableStateOf(false) }
//    LaunchedEffect(Unit) { delay(150); isLoaded = true }
//    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
//    val heartScale by infiniteTransition.animateFloat(
//        initialValue = 1f,
//        targetValue = 1.15f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(400, easing = FastOutSlowInEasing),
//            repeatMode = RepeatMode.Reverse
//        ),
//        label = "heartScale"
//    )
//
//    Box(modifier = Modifier
//        .fillMaxSize()
//        .background(AppTheme.Background)) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .verticalScroll(rememberScrollState())
//                .padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Spacer(modifier = Modifier.height(56.dp))
//            DetailTopBar("Heart Rate", onBackClick)
//            Spacer(modifier = Modifier.height(32.dp))
//            with(sharedScope) {
//                Box(
//                    modifier = Modifier
//                        .size(88.dp)
//                        .background(AppTheme.Heart.copy(alpha = 0.1f), CircleShape)
//                        .sharedBounds(
//                            rememberSharedContentState("bpm_icon_box"),
//                            animScope,
//                            boundsTransform = ProSpatialTransform
//                        ),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        Icons.Rounded.Favorite,
//                        contentDescription = "Heart Rate",
//                        tint = AppTheme.Heart,
//                        modifier = Modifier
//                            .size(48.dp)
//                            .graphicsLayer(scaleX = heartScale, scaleY = heartScale)
//                    )
//                }
//                Spacer(modifier = Modifier.height(16.dp))
//                Text(
//                    "72",
//                    style = TextStyle(brush = AppTheme.BPMGradient),
//                    fontSize = 80.sp,
//                    fontWeight = FontWeight.Black,
//                    modifier = Modifier.sharedBounds(
//                        rememberSharedContentState("bpm_number"),
//                        animScope,
//                        resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
//                        boundsTransform = ProSpatialTransform
//                    )
//                )
//                AnimatedVisibility(
//                    visible = isLoaded,
//                    enter = fadeIn(tween(600))
//                ) {
//                    Text(
//                        "BPM • Resting",
//                        color = AppTheme.Heart,
//                        fontSize = 16.sp,
//                        fontWeight = FontWeight.Bold
//                    )
//                }
//            }
//            Spacer(modifier = Modifier.height(40.dp))
//            val cascadingSpring =
//                spring<IntOffset>(dampingRatio = 0.7f, stiffness = Spring.StiffnessLow)
//            AnimatedVisibility(
//                visible = isLoaded,
//                enter = fadeIn() + slideInVertically(animationSpec = cascadingSpring) { 150 }) { LiveEkgScannerCard() }
//            Spacer(modifier = Modifier.height(16.dp))
//            AnimatedVisibility(
//                visible = isLoaded,
//                enter = fadeIn() + slideInVertically(animationSpec = cascadingSpring) { 250 }) { HeartRateZonesCard() }
//            Spacer(modifier = Modifier.height(48.dp))
//        }
//    }
//}
//
//@Composable
//fun LiveEkgScannerCard() {
//    val infiniteTransition = rememberInfiniteTransition(label = "ekg_sweep")
//    val sweepProgressState = infiniteTransition.animateFloat(
//        initialValue = 0f,
//        targetValue = 1f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(2500, easing = LinearEasing),
//            repeatMode = RepeatMode.Restart
//        ),
//        label = "sweep"
//    )
//    val ekgPath = remember { Path() }
//
//    Box(modifier = Modifier
//        .fillMaxWidth()
//        .cardStyle()
//        .padding(24.dp)) {
//        Column {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    "Live Trace",
//                    color = AppTheme.TextPrimary,
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Bold
//                )
//                Box(
//                    modifier = Modifier
//                        .size(8.dp)
//                        .background(AppTheme.Heart, CircleShape)
//                        .graphicsLayer {
//                            alpha = if (sweepProgressState.value % 0.2f > 0.1f) 1f else 0.3f
//                        })
//            }
//            Spacer(modifier = Modifier.height(24.dp))
//            androidx.compose.foundation.Canvas(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(120.dp)
//                    .clipToBounds()
//            ) {
//                val w = size.width;
//                val h = size.height;
//                val midY = h / 2f;
//                val sweepProgress = sweepProgressState.value
//                val gridAlpha = 0.1f
//                for (i in 1..4) drawLine(
//                    AppTheme.Heart.copy(alpha = gridAlpha),
//                    Offset(0f, h * (i / 5f)),
//                    Offset(w, h * (i / 5f)),
//                    1.dp.toPx()
//                )
//                for (i in 1..9) drawLine(
//                    AppTheme.Heart.copy(alpha = gridAlpha),
//                    Offset(w * (i / 10f), 0f),
//                    Offset(w * (i / 10f), h),
//                    1.dp.toPx()
//                )
//                ekgPath.rewind()
//                val segments = 4;
//                val segmentW = w / segments
//                ekgPath.moveTo(0f, midY)
//                for (i in 0 until segments) {
//                    val startX = i * segmentW
//                    ekgPath.lineTo(startX + segmentW * 0.2f, midY)
//                    ekgPath.lineTo(startX + segmentW * 0.3f, midY - h * 0.1f)
//                    ekgPath.lineTo(startX + segmentW * 0.4f, midY)
//                    ekgPath.lineTo(startX + segmentW * 0.45f, midY + h * 0.2f)
//                    ekgPath.lineTo(startX + segmentW * 0.5f, midY - h * 0.8f)
//                    ekgPath.lineTo(startX + segmentW * 0.55f, midY + h * 0.3f)
//                    ekgPath.lineTo(startX + segmentW * 0.6f, midY)
//                    ekgPath.lineTo(startX + segmentW * 0.7f, midY - h * 0.15f)
//                    ekgPath.lineTo(startX + segmentW * 0.85f, midY)
//                    ekgPath.lineTo(startX + segmentW, midY)
//                }
//                val sweepX = sweepProgress * w
//                drawPath(
//                    path = ekgPath,
//                    brush = Brush.horizontalGradient(
//                        colors = listOf(
//                            Color.Transparent,
//                            AppTheme.Heart.copy(alpha = 0.8f),
//                            AppTheme.Heart
//                        ), startX = max(0f, sweepX - w * 0.4f), endX = sweepX
//                    ),
//                    style = Stroke(3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
//                )
//                drawLine(
//                    brush = Brush.verticalGradient(
//                        listOf(
//                            Color.Transparent,
//                            AppTheme.Heart,
//                            Color.Transparent
//                        )
//                    ),
//                    start = Offset(sweepX, 0f),
//                    end = Offset(sweepX, h),
//                    strokeWidth = 2.dp.toPx()
//                )
//                drawCircle(Color.White, radius = 4.dp.toPx(), center = Offset(sweepX, midY))
//            }
//        }
//    }
//}
//
//@Composable
//fun HeartRateZonesCard() {
//    var loaded by remember { mutableStateOf(false) }
//    LaunchedEffect(Unit) { delay(400); loaded = true }
//    val progress by animateFloatAsState(
//        if (loaded) 1f else 0f,
//        tween(1000, easing = FastOutSlowInEasing),
//        label = "zones"
//    )
//
//    Box(modifier = Modifier
//        .fillMaxWidth()
//        .cardStyle()
//        .padding(24.dp)) {
//        Column {
//            Text(
//                "Time in Zones",
//                color = AppTheme.TextPrimary,
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Bold
//            )
//            Spacer(modifier = Modifier.height(24.dp))
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(24.dp)
//                    .clip(CircleShape)
//                    .background(AppTheme.Track)
//            ) {
//                Row(modifier = Modifier.fillMaxSize()) {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxHeight()
//                            .fillMaxWidth(0.6f * progress)
//                            .background(AppTheme.Steps)
//                    )
//                    Box(
//                        modifier = Modifier
//                            .fillMaxHeight()
//                            .fillMaxWidth(0.6f * progress)
//                            .background(AppTheme.Cals)
//                    )
//                    Box(
//                        modifier = Modifier
//                            .fillMaxHeight()
//                            .fillMaxWidth(0.4f * progress)
//                            .background(AppTheme.Heart)
//                    )
//                }
//            }
//            Spacer(modifier = Modifier.height(20.dp))
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Column {
//                    LegendItem("Resting", AppTheme.Steps); Text(
//                    "12h 45m",
//                    color = AppTheme.TextPrimary,
//                    fontSize = 14.sp,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
//                )
//                }
//                Column {
//                    LegendItem("Fat Burn", AppTheme.Cals); Text(
//                    "2h 15m",
//                    color = AppTheme.TextPrimary,
//                    fontSize = 14.sp,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
//                )
//                }
//                Column {
//                    LegendItem("Peak", AppTheme.Heart); Text(
//                    "45m",
//                    color = AppTheme.TextPrimary,
//                    fontSize = 14.sp,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
//                )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun BpmScoreCard(
//    onHeartClick: () -> Unit,
//    sharedScope: SharedTransitionScope,
//    animScope: AnimatedVisibilityScope
//) {
//    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
//    val heartScale by infiniteTransition.animateFloat(
//        initialValue = 1f,
//        targetValue = 1.15f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(400, easing = FastOutSlowInEasing),
//            repeatMode = RepeatMode.Reverse
//        ),
//        label = "heartScale"
//    )
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .cardStyle()
//            .clickable(remember { MutableInteractionSource() }, null, onClick = onHeartClick)
//            .padding(24.dp)
//    ) {
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Box(modifier = Modifier.size(140.dp), contentAlignment = Alignment.Center) {
//                androidx.compose.foundation.Canvas(modifier = Modifier.size(100.dp)) {
//                    val strokeW = 10.5.dp.toPx()
//                    drawArc(
//                        AppTheme.Track,
//                        0f,
//                        360f,
//                        false,
//                        style = Stroke(strokeW, cap = StrokeCap.Round)
//                    )
//                    drawArc(
//                        AppTheme.BPMGradient,
//                        -90f,
//                        360f * 0.65f,
//                        false,
//                        style = Stroke(strokeW, cap = StrokeCap.Round)
//                    )
//                }
//                with(sharedScope) {
//                    Box(
//                        modifier = Modifier
//                            .size(44.dp)
//                            .background(AppTheme.Heart.copy(alpha = 0.1f), CircleShape)
//                            .sharedBounds(
//                                rememberSharedContentState("bpm_icon_box"),
//                                animScope,
//                                boundsTransform = ProSpatialTransform
//                            ), contentAlignment = Alignment.Center
//                    ) {
//                        Icon(
//                            Icons.Rounded.Favorite,
//                            contentDescription = "Heart Rate",
//                            tint = AppTheme.Heart,
//                            modifier = Modifier
//                                .size(22.dp)
//                                .graphicsLayer(scaleX = heartScale, scaleY = heartScale)
//                        )
//                    }
//                }
//            }
//            Spacer(modifier = Modifier.width(24.dp))
//            Column(modifier = Modifier.weight(1f)) {
//                Text(
//                    "HEART RATE",
//                    color = AppTheme.TextSecondary,
//                    fontSize = 12.sp,
//                    fontWeight = FontWeight.Bold,
//                    letterSpacing = 2.sp
//                )
//                with(sharedScope) {
//                    Text(
//                        "72",
//                        style = TextStyle(brush = AppTheme.BPMGradient),
//                        fontSize = 48.sp,
//                        fontWeight = FontWeight.Black,
//                        modifier = Modifier.sharedBounds(
//                            rememberSharedContentState("bpm_number"),
//                            animScope,
//                            resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds(),
//                            boundsTransform = ProSpatialTransform
//                        )
//                    )
//                }
//                Text(
//                    "BPM • Resting",
//                    color = AppTheme.Heart,
//                    fontSize = 11.sp,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun StressScoreCard(sharedScope: SharedTransitionScope, animScope: AnimatedVisibilityScope) {
//    val infiniteTransition = rememberInfiniteTransition(label = "breathe")
//    val breatheScale by infiniteTransition.animateFloat(
//        initialValue = 0.6f,
//        targetValue = 1.3f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(2500, easing = FastOutSlowInEasing),
//            repeatMode = RepeatMode.Reverse
//        ),
//        label = "breatheScale"
//    )
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .cardStyle()
//            .clickable(remember { MutableInteractionSource() }, null) { }
//            .padding(24.dp)
//    ) {
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Box(modifier = Modifier.size(140.dp), contentAlignment = Alignment.Center) {
//                androidx.compose.foundation.Canvas(modifier = Modifier.size(100.dp)) {
//                    val strokeW = 10.5.dp.toPx()
//                    drawArc(
//                        AppTheme.Track,
//                        0f,
//                        360f,
//                        false,
//                        style = Stroke(strokeW, cap = StrokeCap.Round)
//                    )
//                    drawArc(
//                        AppTheme.StressGradient,
//                        -90f,
//                        360f * 0.32f,
//                        false,
//                        style = Stroke(strokeW, cap = StrokeCap.Round)
//                    )
//                }
//                Box(
//                    modifier = Modifier
//                        .size(44.dp)
//                        .background(AppTheme.Stress.copy(alpha = 0.1f), CircleShape),
//                    contentAlignment = Alignment.Center
//                ) {
//                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
//                        drawCircle(
//                            AppTheme.Stress.copy(alpha = 0.3f),
//                            radius = 10.dp.toPx() * breatheScale
//                        )
//                        drawCircle(AppTheme.Stress, radius = 5.dp.toPx())
//                    }
//                }
//            }
//            Spacer(modifier = Modifier.width(24.dp))
//            Column(modifier = Modifier.weight(1f)) {
//                Text(
//                    "STRESS LEVEL",
//                    color = AppTheme.TextSecondary,
//                    fontSize = 12.sp,
//                    fontWeight = FontWeight.Bold,
//                    letterSpacing = 2.sp
//                )
//                Text(
//                    "32",
//                    style = TextStyle(brush = AppTheme.StressGradient),
//                    fontSize = 48.sp,
//                    fontWeight = FontWeight.Black
//                )
//                Text(
//                    "Relaxed • Avg today",
//                    color = AppTheme.Stress,
//                    fontSize = 11.sp,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun SleepScoreCard(
//    onSleepClick: () -> Unit,
//    sharedScope: SharedTransitionScope,
//    animScope: AnimatedVisibilityScope
//) {
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .cardStyle()
//            .clickable(remember { MutableInteractionSource() }, null, onClick = onSleepClick)
//            .padding(24.dp)
//    ) {
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Box(modifier = Modifier.size(140.dp), contentAlignment = Alignment.Center) {
//                androidx.compose.foundation.Canvas(modifier = Modifier.size(100.dp)) {
//                    val strokeW = 10.5.dp.toPx()
//                    drawArc(
//                        AppTheme.Track,
//                        0f,
//                        360f,
//                        false,
//                        style = Stroke(strokeW, cap = StrokeCap.Round)
//                    )
//                    drawArc(
//                        AppTheme.SleepGradient,
//                        -90f,
//                        360f * 0.92f,
//                        false,
//                        style = Stroke(strokeW, cap = StrokeCap.Round)
//                    )
//                }
//                Box(
//                    modifier = Modifier
//                        .size(44.dp)
//                        .background(AppTheme.Sleep.copy(alpha = 0.1f), CircleShape),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        "Zzz",
//                        style = TextStyle(brush = AppTheme.SleepGradient),
//                        fontSize = 16.sp,
//                        fontWeight = FontWeight.ExtraBold,
//                        letterSpacing = 0.5.sp,
//                        modifier = Modifier.padding(top = 2.dp, start = 2.dp)
//                    )
//                }
//            }
//            Spacer(modifier = Modifier.width(24.dp))
//            Column(modifier = Modifier.weight(1f)) {
//                with(sharedScope) {
//                    Text(
//                        "SLEEP",
//                        color = AppTheme.TextSecondary,
//                        fontSize = 12.sp,
//                        fontWeight = FontWeight.Bold,
//                        letterSpacing = 2.sp,
//                        modifier = Modifier.sharedBounds(
//                            rememberSharedContentState("sleep_label"),
//                            animScope,
//                            resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds()
//                        )
//                    )
//                    Text(
//                        "92",
//                        style = TextStyle(brush = AppTheme.SleepGradient),
//                        fontSize = 48.sp,
//                        fontWeight = FontWeight.Black,
//                        modifier = Modifier.sharedBounds(
//                            rememberSharedContentState("sleep_number"),
//                            animScope,
//                            resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds()
//                        )
//                    )
//                }
//                Text(
//                    "Restful • 7h 20m",
//                    color = AppTheme.Sleep,
//                    fontSize = 11.sp,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun SleepDetailsScreen(
//    onBackClick: () -> Unit,
//    sharedScope: SharedTransitionScope,
//    animScope: AnimatedVisibilityScope
//) {
//    var isLoaded by remember { mutableStateOf(false) }
//    LaunchedEffect(Unit) { delay(150); isLoaded = true }
//
//    Box(modifier = Modifier
//        .fillMaxSize()
//        .background(AppTheme.Background)) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .verticalScroll(rememberScrollState())
//                .padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Spacer(modifier = Modifier.height(56.dp))
//            DetailTopBar("Sleep Analysis", onBackClick)
//            Spacer(modifier = Modifier.height(24.dp))
//
//            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                with(sharedScope) {
//                    Text(
//                        "SLEEP",
//                        color = AppTheme.TextSecondary,
//                        fontSize = 13.sp,
//                        fontWeight = FontWeight.Bold,
//                        letterSpacing = 2.sp,
//                        modifier = Modifier.sharedBounds(
//                            rememberSharedContentState("sleep_label"),
//                            animScope,
//                            resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds()
//                        )
//                    )
//                    Text(
//                        "92",
//                        style = TextStyle(brush = AppTheme.SleepGradient),
//                        fontSize = 72.sp,
//                        fontWeight = FontWeight.Black,
//                        modifier = Modifier.sharedBounds(
//                            rememberSharedContentState("sleep_number"),
//                            animScope,
//                            resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds()
//                        )
//                    )
//                }
//                AnimatedVisibility(
//                    visible = isLoaded,
//                    enter = fadeIn(tween(600))
//                ) {
//                    Text(
//                        "Restful • 7h 20m",
//                        color = AppTheme.TextSecondary,
//                        fontSize = 15.sp,
//                        fontWeight = FontWeight.Medium
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(32.dp))
//            SleepGraphCard(isLoaded)
//            Spacer(modifier = Modifier.height(16.dp))
//            AnimatedSleepStatCards(isLoaded)
//            Spacer(modifier = Modifier.height(48.dp))
//        }
//    }
//}
//
//@Stable
//enum class SleepStage(val level: Int, val color: Color) {
//    AWAKE(0, AppTheme.SleepAwake), REM(
//        1,
//        AppTheme.SleepRem
//    ),
//    LIGHT(2, AppTheme.SleepLight), DEEP(3, AppTheme.SleepDeep)
//}
//
//@Stable
//data class SleepSegment(val stage: SleepStage, val durationMinutes: Float)
//
//private val mockSleepData = listOf(
//    SleepSegment(SleepStage.AWAKE, 15f),
//    SleepSegment(SleepStage.LIGHT, 25f),
//    SleepSegment(SleepStage.DEEP, 45f),
//    SleepSegment(SleepStage.LIGHT, 20f),
//    SleepSegment(SleepStage.REM, 25f),
//    SleepSegment(SleepStage.LIGHT, 30f),
//    SleepSegment(SleepStage.DEEP, 40f),
//    SleepSegment(SleepStage.LIGHT, 20f),
//    SleepSegment(SleepStage.AWAKE, 5f),
//    SleepSegment(SleepStage.LIGHT, 25f),
//    SleepSegment(SleepStage.REM, 35f),
//    SleepSegment(SleepStage.LIGHT, 45f),
//    SleepSegment(SleepStage.REM, 30f),
//    SleepSegment(SleepStage.AWAKE, 10f)
//)
//
//@Composable
//fun SleepGraphCard(isLoaded: Boolean) {
//    val traceProgress by animateFloatAsState(
//        if (isLoaded) 1f else 0f,
//        tween(2200, easing = FastOutSlowInEasing),
//        label = "trace"
//    )
//    val bloomProgress by animateFloatAsState(
//        if (isLoaded) 1f else 0f,
//        tween(1500, delayMillis = 1000, easing = FastOutSlowInEasing),
//        label = "bloom"
//    )
//
//    val continuousPath = remember { Path() }
//    val pathMeasure = remember { PathMeasure() }
//
//    Box(modifier = Modifier
//        .fillMaxWidth()
//        .cardStyle()
//        .padding(24.dp)) {
//        Column {
//            Text(
//                "Sleep Stages",
//                color = AppTheme.TextPrimary,
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Bold
//            )
//            Spacer(modifier = Modifier.height(24.dp))
//            androidx.compose.foundation.Canvas(modifier = Modifier
//                .fillMaxWidth()
//                .height(140.dp)) {
//                val totalDuration = mockSleepData.sumOf { it.durationMinutes.toDouble() }.toFloat()
//                val targetStroke = 12.dp.toPx()
//                val currentStroke = targetStroke * (0.6f + 0.4f * bloomProgress)
//                val verticalStep = (size.height - targetStroke) / 3f
//                fun getY(stage: SleepStage) = targetStroke / 2f + (stage.level * verticalStep)
//                val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
//                for (i in 0..3) drawLine(
//                    AppTheme.Track.copy(alpha = 0.4f),
//                    Offset(0f, targetStroke / 2f + (i * verticalStep)),
//                    Offset(size.width, targetStroke / 2f + (i * verticalStep)),
//                    1.dp.toPx(),
//                    pathEffect = dashEffect
//                )
//
//                continuousPath.rewind()
//                var currentX = 0f
//                for ((index, segment) in mockSleepData.withIndex()) {
//                    val segmentW = (segment.durationMinutes / totalDuration) * size.width
//                    val y = getY(segment.stage)
//                    if (index == 0) continuousPath.moveTo(currentX, y) else continuousPath.lineTo(
//                        currentX,
//                        y
//                    )
//                    continuousPath.lineTo(currentX + segmentW, y)
//                    currentX += segmentW
//                }
//
//                pathMeasure.setPath(continuousPath, forceClosed = false)
//                val currentDistance = pathMeasure.length * traceProgress
//                val sparkPos = pathMeasure.getPosition(currentDistance)
//
//                clipRect(right = if (traceProgress >= 0.99f) size.width else sparkPos.x) {
//                    var pX = 0f;
//                    var prevX = -1f;
//                    var prevY = -1f;
//                    var prevColor = Color.Transparent
//                    for (segment in mockSleepData) {
//                        val segmentW = (segment.durationMinutes / totalDuration) * size.width
//                        val y = getY(segment.stage)
//                        if (prevX >= 0) drawLine(
//                            Brush.verticalGradient(
//                                if (prevY < y) listOf(
//                                    prevColor,
//                                    segment.stage.color
//                                ) else listOf(segment.stage.color, prevColor),
//                                startY = min(prevY, y),
//                                endY = max(prevY, y)
//                            ), Offset(pX, prevY), Offset(pX, y), currentStroke * 0.4f
//                        )
//                        prevX = pX; prevY = y; prevColor = segment.stage.color; pX += segmentW
//                    }
//                    pX = 0f
//                    for (segment in mockSleepData) {
//                        val segmentW = (segment.durationMinutes / totalDuration) * size.width
//                        val y = getY(segment.stage)
//                        val startX = pX + currentStroke / 2f;
//                        val endX = pX + segmentW - currentStroke / 2f
//                        if (startX <= endX) drawLine(
//                            segment.stage.color,
//                            Offset(startX, y),
//                            Offset(endX, y),
//                            currentStroke,
//                            StrokeCap.Round
//                        ) else drawLine(
//                            segment.stage.color,
//                            Offset(pX + segmentW / 2f, y),
//                            Offset(pX + segmentW / 2f, y),
//                            currentStroke,
//                            StrokeCap.Round
//                        )
//                        pX += segmentW
//                    }
//                }
//                if (traceProgress in 0.01f..0.99f) {
//                    val tailPath = Path()
//                    pathMeasure.getSegment(
//                        max(0f, currentDistance - 150f),
//                        currentDistance,
//                        tailPath,
//                        true
//                    )
//                    drawPath(
//                        tailPath,
//                        AppTheme.SleepRem,
//                        style = Stroke(4.dp.toPx(), cap = StrokeCap.Round)
//                    )
//                    drawCircle(AppTheme.SleepRem.copy(alpha = 0.5f), 14.dp.toPx(), sparkPos)
//                    drawCircle(Color.White, 5.dp.toPx(), sparkPos)
//                }
//            }
//            Spacer(modifier = Modifier.height(16.dp))
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Text(
//                    "23:30",
//                    color = AppTheme.TextSecondary.copy(alpha = bloomProgress),
//                    fontSize = 12.sp,
//                    fontWeight = FontWeight.Bold
//                )
//                Text(
//                    "07:15",
//                    color = AppTheme.TextSecondary.copy(alpha = bloomProgress),
//                    fontSize = 12.sp,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//            Spacer(modifier = Modifier.height(24.dp))
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                LegendItem("Awake", AppTheme.SleepAwake, bloomProgress); LegendItem(
//                "REM",
//                AppTheme.SleepRem,
//                bloomProgress
//            )
//                LegendItem("Light", AppTheme.SleepLight, bloomProgress); LegendItem(
//                "Deep",
//                AppTheme.SleepDeep,
//                bloomProgress
//            )
//            }
//        }
//    }
//}
//
//@Composable
//fun DetailsScreen(
//    onBackClick: () -> Unit,
//    sharedScope: SharedTransitionScope,
//    animScope: AnimatedVisibilityScope,
//    morphProgressProvider: () -> Float
//) {
//    var isLoaded by remember { mutableStateOf(false) }
//    LaunchedEffect(Unit) { delay(150); isLoaded = true }
//    Box(modifier = Modifier
//        .fillMaxSize()
//        .background(AppTheme.Background)) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(horizontal = 24.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Spacer(modifier = Modifier.height(56.dp))
//            DetailTopBar("Vitality Details", onBackClick)
//            Spacer(modifier = Modifier.height(48.dp))
//            Box(contentAlignment = Alignment.Center) {
//                with(sharedScope) {
//                    BioOrbCanvas(
//                        modifier = Modifier
//                            .size(340.dp)
//                            .shadow(
//                                24.dp,
//                                CircleShape,
//                                ambientColor = Color(0x05000000),
//                                spotColor = Color(0x0A000000)
//                            )
//                            .background(AppTheme.Surface, CircleShape)
//                            .sharedElement(rememberSharedContentState("vitality_orb"), animScope),
//                        stepsProgress = 0.8f,
//                        calsProgress = 0.65f,
//                        actProgress = 0.9f,
//                        morphProgressProvider = morphProgressProvider
//                    )
//                }
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    with(sharedScope) {
//                        Text(
//                            "VITALITY",
//                            color = AppTheme.TextSecondary,
//                            fontSize = 16.sp,
//                            fontWeight = FontWeight.Bold,
//                            letterSpacing = 2.sp,
//                            modifier = Modifier.sharedBounds(
//                                rememberSharedContentState("vitality_label"),
//                                animScope,
//                                resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds()
//                            )
//                        )
//                        Text(
//                            "88",
//                            style = TextStyle(brush = AppTheme.VitalityGradient),
//                            fontSize = 80.sp,
//                            fontWeight = FontWeight.Black,
//                            modifier = Modifier.sharedBounds(
//                                rememberSharedContentState("vitality_number"),
//                                animScope,
//                                resizeMode = SharedTransitionScope.ResizeMode.scaleToBounds()
//                            )
//                        )
//                    }
//                }
//            }
//            Spacer(modifier = Modifier.height(48.dp))
//            AnimatedStatCards(isLoaded)
//        }
//    }
//}
//
//@Composable
//fun BioOrbCanvas(
//    modifier: Modifier,
//    stepsProgress: Float,
//    calsProgress: Float,
//    actProgress: Float,
//    morphProgressProvider: () -> Float
//) {
//    val paths = remember { Array(3) { Path() } }
//    val dstPaths = remember { Array(3) { Path() } }
//    val pathMeasure = remember { PathMeasure() }
//
//    androidx.compose.foundation.Canvas(modifier = modifier.padding(8.dp)) {
//        val minDim = size.minDimension
//        val morph = morphProgressProvider()
//        val heartStroke = minDim * 0.085f;
//        val circleStroke = minDim * 0.045f
//        val strokeWidth = heartStroke + (circleStroke - heartStroke) * morph
//        val offsetAmount = strokeWidth + minDim * 0.015f
//        val d = (minDim * 0.085f) * (1f - morph)
//        val r0 = (minDim * 0.31f) * (1f - morph) + (minDim / 2f - strokeWidth) * morph
//        val r1 = r0 - offsetAmount;
//        val r2 = r1 - offsetAmount
//        val rCInner = minDim * 0.035f;
//        val rC0 = rCInner + 2 * offsetAmount;
//        val rC1 = rCInner + offsetAmount;
//        val rC2 = rCInner
//        val alphaDeg = 45f + 45f * morph;
//        val alpha = alphaDeg * PI.toFloat() / 180f
//        val sinA = sin(alpha);
//        val dOverTanA = d * cos(alpha) / sinA
//        val pivotX = size.width / 2f;
//        val pivotY =
//            size.height / 2f - (((r0 / sinA + dOverTanA - rC0 / sinA) + rC0 - r0) / 2f) * (1f - morph)
//
//        fun buildRing(path: Path, R: Float, rC: Float) {
//            path.rewind();
//            val cleftYOffset = -sqrt(max(0f, R * R - d * d));
//            val vY = pivotY + R / sinA + dOverTanA
//            path.moveTo(pivotX, pivotY + cleftYOffset)
//            val startAngleR = atan2(cleftYOffset, -d) * RAD_TO_DEG
//            path.arcTo(
//                Rect(pivotX + d - R, pivotY - R, pivotX + d + R, pivotY + R),
//                startAngleR,
//                if (alphaDeg - startAngleR < 0f) alphaDeg - startAngleR + 360f else alphaDeg - startAngleR,
//                false
//            )
//            path.arcTo(
//                Rect(pivotX - rC, (vY - rC / sinA) - rC, pivotX + rC, (vY - rC / sinA) + rC),
//                alphaDeg,
//                180f - 2f * alphaDeg,
//                false
//            )
//            val startAngleL = 180f - alphaDeg;
//            val endAngleL = atan2(cleftYOffset, d) * RAD_TO_DEG
//            path.arcTo(
//                Rect(pivotX - d - R, pivotY - R, pivotX - d + R, pivotY + R),
//                startAngleL,
//                if (endAngleL - startAngleL < 0f) endAngleL - startAngleL + 360f else endAngleL - startAngleL,
//                false
//            )
//            path.close()
//        }
//
//        buildRing(paths[0], r0, rC0); buildRing(paths[1], r1, rC1); buildRing(paths[2], r2, rC2)
//
//        fun extractSegment(src: Path, dst: Path, progress: Float) {
//            dst.rewind()
//            if (progress > 0.001f) {
//                pathMeasure.setPath(src, forceClosed = false); pathMeasure.getSegment(
//                    0f,
//                    pathMeasure.length * progress,
//                    dst,
//                    true
//                )
//            }
//        }
//
//        extractSegment(paths[0], dstPaths[0], stepsProgress); extractSegment(
//        paths[1],
//        dstPaths[1],
//        calsProgress
//    ); extractSegment(paths[2], dstPaths[2], actProgress)
//        val pathStyle = Stroke(strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
//        drawPath(paths[0], AppTheme.Steps.copy(alpha = 0.2f), style = pathStyle); drawPath(
//        paths[1],
//        AppTheme.Cals.copy(alpha = 0.2f),
//        style = pathStyle
//    ); drawPath(paths[2], AppTheme.Act.copy(alpha = 0.2f), style = pathStyle)
//        drawPath(dstPaths[0], AppTheme.Steps, style = pathStyle); drawPath(
//        dstPaths[1],
//        AppTheme.Cals,
//        style = pathStyle
//    ); drawPath(dstPaths[2], AppTheme.Act, style = pathStyle)
//    }
//}
//
//@Composable
//fun ProfileScreen(
//    onBackClick: () -> Unit,
//    sharedScope: SharedTransitionScope,
//    animScope: AnimatedVisibilityScope
//) {
//    Box(modifier = Modifier
//        .fillMaxSize()
//        .background(AppTheme.Background)) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(horizontal = 24.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Spacer(modifier = Modifier.height(56.dp))
//            DetailTopBar("Profile", onBackClick)
//            Spacer(modifier = Modifier.height(48.dp))
//
//            with(sharedScope) {
//                Box(
//                    modifier = Modifier
//                        .sharedElement(
//                            rememberSharedContentState("profile_avatar"),
//                            animScope
//                        )
//                        .size(120.dp)
//                        .shadow(
//                            16.dp,
//                            CircleShape,
//                            ambientColor = Color(0x05000000),
//                            spotColor = Color(0x0A000000)
//                        )
//                        .background(AppTheme.Surface, CircleShape), contentAlignment = Alignment.Center
//                ) {
//                    Box(
//                        modifier = Modifier
//                            .size(100.dp)
//                            .background(AppTheme.Track, CircleShape),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Icon(
//                            Icons.Rounded.Person,
//                            contentDescription = "Profile",
//                            tint = AppTheme.TextSecondary,
//                            modifier = Modifier.size(56.dp)
//                        )
//                    }
//                }
//            }
//            Spacer(modifier = Modifier.height(24.dp))
//            Text(
//                "Kyriakos",
//                style = TextStyle(brush = AppTheme.TextGradient),
//                fontSize = 28.sp,
//                fontWeight = FontWeight.ExtraBold
//            )
//            Text(
//                "Pro Member • Since 2026",
//                color = AppTheme.TextSecondary,
//                fontSize = 14.sp,
//                fontWeight = FontWeight.Medium
//            )
//            Spacer(modifier = Modifier.height(48.dp))
//            StatCard(
//                "Account Settings",
//                "Manage",
//                0f,
//                Icons.Rounded.Settings,
//                AppTheme.TextSecondary
//            )
//            StatCard(
//                "Health Goals",
//                "Active",
//                0.5f,
//                Icons.Rounded.LocalFireDepartment,
//                AppTheme.Cals
//            )
//        }
//    }
//}
//
//@Composable
//fun LegendItem(label: String, color: Color, alpha: Float = 1f) {
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = Modifier.graphicsLayer { this.alpha = alpha }) {
//        Box(modifier = Modifier
//            .size(10.dp)
//            .background(color, CircleShape))
//        Spacer(modifier = Modifier.width(6.dp))
//        Text(
//            label,
//            color = AppTheme.TextSecondary,
//            fontSize = 12.sp,
//            fontWeight = FontWeight.Medium
//        )
//    }
//}
//
//@Composable
//fun AnimatedStatCards(isLoaded: Boolean) {
//    AnimatedVisibility(
//        visible = isLoaded,
//        enter = slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(400)) + fadeIn(
//            tween(400)
//        )
//    ) { StatCard("Steps", "12,430", 0.8f, Icons.Rounded.DirectionsWalk, AppTheme.Steps) }
//    AnimatedVisibility(
//        visible = isLoaded,
//        enter = slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(500)) + fadeIn(
//            tween(500)
//        )
//    ) { StatCard("Calories", "2,100", 0.65f, Icons.Rounded.LocalFireDepartment, AppTheme.Cals) }
//    AnimatedVisibility(
//        visible = isLoaded,
//        enter = slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(600)) + fadeIn(
//            tween(600)
//        )
//    ) { StatCard("Activity", "45min", 0.9f, Icons.Rounded.Bolt, AppTheme.Act) }
//}
//
//@Composable
//fun AnimatedSleepStatCards(isLoaded: Boolean) {
//    AnimatedVisibility(
//        visible = isLoaded,
//        enter = slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(500)) + fadeIn(
//            tween(500)
//        )
//    ) { StatCard("Actual Sleep", "7h 20m", 0.92f, Icons.Rounded.CheckCircle, AppTheme.SleepLight) }
//    AnimatedVisibility(
//        visible = isLoaded,
//        enter = slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(600)) + fadeIn(
//            tween(600)
//        )
//    ) { StatCard("Deep Sleep", "1h 45m", 0.85f, Icons.Rounded.Favorite, AppTheme.SleepDeep) }
//    AnimatedVisibility(
//        visible = isLoaded,
//        enter = slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(700)) + fadeIn(
//            tween(700)
//        )
//    ) { StatCard("REM Sleep", "2h 10m", 0.75f, Icons.Rounded.Star, AppTheme.SleepRem) }
//}
//
//@Composable
//fun StatCard(
//    label: String,
//    value: String,
//    targetProgress: Float,
//    icon: ImageVector,
//    accentColor: Color
//) {
//    var loaded by remember { mutableStateOf(false) }
//    LaunchedEffect(Unit) { loaded = true }
//    val progress by animateFloatAsState(if (loaded) targetProgress else 0f, ScaleSpring, label = "")
//
//    Box(modifier = Modifier
//        .fillMaxWidth()
//        .padding(vertical = 8.dp)
//        .cardStyle()
//        .padding(20.dp)) {
//        Column {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Box(
//                    modifier = Modifier
//                        .size(40.dp)
//                        .background(accentColor.copy(alpha = 0.1f), CircleShape),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        icon,
//                        contentDescription = null,
//                        tint = accentColor,
//                        modifier = Modifier.size(22.dp)
//                    )
//                }
//                Spacer(modifier = Modifier.width(16.dp))
//                Text(
//                    label,
//                    color = AppTheme.TextSecondary,
//                    fontSize = 17.sp,
//                    fontWeight = FontWeight.SemiBold
//                )
//                Spacer(modifier = Modifier.weight(1f))
//                Text(
//                    value,
//                    color = AppTheme.TextPrimary,
//                    fontSize = 22.sp,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//            Spacer(modifier = Modifier.height(18.dp))
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(8.dp)
//                    .clip(CircleShape)
//                    .background(AppTheme.Track)
//            ) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth(progress)
//                        .fillMaxHeight()
//                        .background(accentColor, CircleShape)
//                )
//            }
//        }
//    }
//}