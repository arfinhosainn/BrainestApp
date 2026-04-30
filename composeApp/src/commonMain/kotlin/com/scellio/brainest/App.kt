package com.scellio.brainest

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import brainest.composeapp.generated.resources.Res
import brainest.composeapp.generated.resources.ic_cards
import brainest.composeapp.generated.resources.ic_scan
import brainest.composeapp.generated.resources.ic_stars
import com.brainest.presentation.navigation.OnboardingGraphRoutes
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.domain.onboarding.OnboardingData
import com.scelio.brainest.domain.onboarding.OnboardingStore
import com.scelio.brainest.presentation.navigation.AuthGraphRoutes
import com.scelio.brainest.presentation.navigation.ChatGraphRoutes
import com.scelio.brainest.presentation.navigation.FlashcardsGraphRoutes
import com.scelio.brainest.presentation.navigation.HomeGraphRoutes
import com.scelio.brainest.presentation.navigation.ScanGraphRoutes
import com.scelio.brainest.presentation.util.ObserveAsEvents
import com.scellio.brainest.localization.AppLanguageState
import com.scellio.brainest.localization.applyAppLanguage
import com.scellio.brainest.localization.languageIdToTag
import com.scellio.brainest.navigation.BrainestBottomNavigationBar
import com.scellio.brainest.navigation.DeepLinkListener
import com.scellio.brainest.navigation.NavigationRoot
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App(
    onAutheticationChecked: () -> Unit = {},
    viewModel: MainViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    val onboardingStore = koinInject<OnboardingStore>()
    DeepLinkListener(navController)

    val state by viewModel.state.collectAsStateWithLifecycle()
    val onboardingData by onboardingStore.data.collectAsStateWithLifecycle(initialValue = OnboardingData())
    val appLanguageTag by AppLanguageState.languageTag.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val hasCompletedOnboarding = onboardingData.isCompleted()
    val isStartupReady = !state.isCheckingAuth
    val hasRenderedApp = remember { mutableStateOf(false) }
    val shouldRenderApp = isStartupReady || hasRenderedApp.value
    var homeFabExpanded by rememberSaveable { mutableStateOf(false) }
    val startDestination = when {
        state.isLoggedIn -> HomeGraphRoutes.Graph
        hasCompletedOnboarding -> AuthGraphRoutes.Graph
        else -> OnboardingGraphRoutes.Graph
    }
    val homePrefix = HomeGraphRoutes::class.qualifiedName
    val authPrefix = AuthGraphRoutes::class.qualifiedName
    val onboardingPrefix = OnboardingGraphRoutes::class.qualifiedName
    val isOnHomeGraph = homePrefix != null && currentRoute?.startsWith(homePrefix) == true
    val isOnAuthGraph = authPrefix != null && currentRoute?.startsWith(authPrefix) == true
    val isOnOnboardingGraph = onboardingPrefix != null && currentRoute?.startsWith(onboardingPrefix) == true
    val isRouteAligned = when {
        state.isLoggedIn -> isOnHomeGraph
        hasCompletedOnboarding -> isOnAuthGraph
        else -> isOnOnboardingGraph
    }
    val fabOffsetY = if (isIosPlatform()) (-36).dp else (-20).dp
    val canDismissSplash = isStartupReady
    val hasDismissedSplash = remember { mutableStateOf(false) }

    LaunchedEffect(onboardingData.languageId) {
        val persistedLanguageId = onboardingData.languageId ?: return@LaunchedEffect
        val languageTag = languageIdToTag(persistedLanguageId)
        AppLanguageState.update(languageTag)
        applyAppLanguage(languageTag)
    }

    LaunchedEffect(canDismissSplash) {
        if (canDismissSplash && !hasDismissedSplash.value) {
            hasDismissedSplash.value = true
            onAutheticationChecked()
        }
    }
    LaunchedEffect(isStartupReady) {
        if (isStartupReady) {
            hasRenderedApp.value = true
        }
    }
    LaunchedEffect(isStartupReady, state.isLoggedIn, hasCompletedOnboarding, currentRoute, hasDismissedSplash.value) {
        if (!isStartupReady || currentRoute == null || hasDismissedSplash.value || isRouteAligned) {
            return@LaunchedEffect
        }

        when {
            state.isLoggedIn -> {
                navController.navigate(HomeGraphRoutes.Graph) {
                    launchSingleTop = true
                    if (isOnAuthGraph) {
                        popUpTo(AuthGraphRoutes.Graph) { inclusive = true }
                    } else if (isOnOnboardingGraph) {
                        popUpTo(OnboardingGraphRoutes.Graph) { inclusive = true }
                    }
                }
            }

            !state.isLoggedIn && hasCompletedOnboarding -> {
                navController.navigate(AuthGraphRoutes.Graph) {
                    launchSingleTop = true
                    if (isOnHomeGraph) {
                        popUpTo(HomeGraphRoutes.Graph) { inclusive = true }
                    } else if (isOnOnboardingGraph) {
                        popUpTo(OnboardingGraphRoutes.Graph) { inclusive = true }
                    }
                }
            }

            else -> {
                navController.navigate(OnboardingGraphRoutes.Graph) {
                    launchSingleTop = true
                    if (isOnHomeGraph) {
                        popUpTo(HomeGraphRoutes.Graph) { inclusive = true }
                    } else if (isOnAuthGraph) {
                        popUpTo(AuthGraphRoutes.Graph) { inclusive = true }
                    }
                }
            }
        }
    }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is MainEvent.OnSessionExpired -> {
                navController.navigate(AuthGraphRoutes.Graph) {
                    popUpTo(AuthGraphRoutes.Graph) {
                        inclusive = false
                    }
                }
            }
        }
    }

    BrainestTheme {
        // Consume the app language state so locale updates trigger recomposition.
        appLanguageTag
        if (shouldRenderApp) {
            val chatDetailRoutePrefix = ChatGraphRoutes.ChatDetailRoute::class.qualifiedName
            val isChatDetailRoute = chatDetailRoutePrefix != null &&
                currentRoute?.startsWith(chatDetailRoutePrefix) == true
            val chatListRoute = ChatGraphRoutes.ChatListRoute::class.qualifiedName
            val isChatListRoute = currentRoute == chatListRoute ||
                currentRoute?.endsWith(".ChatListRoute") == true
            val quizRoutePrefix = FlashcardsGraphRoutes.QuizSession::class.qualifiedName
            val isQuizRoute = (quizRoutePrefix != null && currentRoute?.startsWith(quizRoutePrefix) == true) ||
                currentRoute?.endsWith(".QuizSession") == true
            val flashcardsSessionRoutePrefix = FlashcardsGraphRoutes.Session::class.qualifiedName
            val isFlashcardsSessionRoute = (flashcardsSessionRoutePrefix != null &&
                currentRoute?.startsWith(flashcardsSessionRoutePrefix) == true) ||
                currentRoute?.endsWith(".Session") == true
            val settingsRoute = HomeGraphRoutes.Settings::class.qualifiedName
            val isSettingsRoute = currentRoute == settingsRoute ||
                currentRoute?.endsWith(".Settings") == true
            val scanRoutePrefix = ScanGraphRoutes.Scan::class.qualifiedName
            val isScanRoute = scanRoutePrefix != null && currentRoute?.startsWith(scanRoutePrefix) == true
            val hideBottomBar = currentRoute == FlashcardsGraphRoutes.AudioRecording::class.qualifiedName ||
                currentRoute?.endsWith(".AudioRecording") == true ||
                isChatDetailRoute ||
                isChatListRoute ||
                isQuizRoute ||
                isFlashcardsSessionRoute ||
                isSettingsRoute ||
                isScanRoute
            val mainGraphPrefixes = listOfNotNull(
                HomeGraphRoutes::class.qualifiedName,
                ChatGraphRoutes::class.qualifiedName,
                FlashcardsGraphRoutes::class.qualifiedName
            )
            val isMainGraphRoute = mainGraphPrefixes.any { prefix ->
                currentRoute?.startsWith(prefix) == true
            }
            val homeGraphRoute = HomeGraphRoutes.Graph::class.qualifiedName
            val isHomeScreenRoute = currentRoute == HomeGraphRoutes.Home::class.qualifiedName ||
                currentRoute?.endsWith(".Home") == true ||
                currentRoute == homeGraphRoute ||
                (homeGraphRoute != null && currentRoute?.startsWith(homeGraphRoute) == true)
            val statusBarTopInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
            LaunchedEffect(isHomeScreenRoute) {
                if (!isHomeScreenRoute) homeFabExpanded = false
            }
            val showBottomBar = (state.isLoggedIn || isMainGraphRoute) && !hideBottomBar
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(statusBarTopInset)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        if (showBottomBar) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    BrainestBottomNavigationBar(
                                        modifier = Modifier.weight(1f),
                                        onItemSelected = { index ->
                                            when (index) {
                                                0 -> navController.navigate(HomeGraphRoutes.Graph) {
                                                    launchSingleTop = true
                                                }
                                                1 -> navController.navigate(ScanGraphRoutes.Scan) {
                                                    launchSingleTop = true
                                                }
                                                2 -> navController.navigate(ChatGraphRoutes.ChatDetailRoute()) {
                                                    launchSingleTop = true
                                                }
                                                3 -> navController.navigate(FlashcardsGraphRoutes.Graph) {
                                                    launchSingleTop = true
                                                }
                                                else -> Unit
                                            }
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(84.dp))
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    val layoutDirection = LocalLayoutDirection.current
                    val navHostPadding = PaddingValues(
                        start = innerPadding.calculateStartPadding(layoutDirection),
                        top = innerPadding.calculateTopPadding(),
                        end = innerPadding.calculateEndPadding(layoutDirection),
                        bottom = 0.dp
                    )
                    Box(modifier = Modifier.fillMaxSize()) {
                        NavigationRoot(
                            navController = navController,
                            startDestination = startDestination,
                            modifier = Modifier.padding(navHostPadding)
                        )

                        if (isHomeScreenRoute && homeFabExpanded) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        homeFabExpanded = false
                                    }
                            )
                        }
                    }
                }

                if (showBottomBar) {
                    if (isHomeScreenRoute) {
                        ExpandableHomeFab(
                            expanded = homeFabExpanded,
                            onExpandedChange = { homeFabExpanded = it },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 18.dp)
                                .offset(y = fabOffsetY),
                            onRecordAudioClick = {
                                navController.navigate(FlashcardsGraphRoutes.AudioRecording) {
                                    launchSingleTop = true
                                }
                            },
                            onUploadAudioClick = {
                                navController.navigate(FlashcardsGraphRoutes.Graph) {
                                    launchSingleTop = true
                                }
                            },
                            onUploadDocumentClick = {
                                navController.navigate(FlashcardsGraphRoutes.Graph) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    } else {
                        FloatingActionButton(
                            onClick = {
                                navController.navigate(ScanGraphRoutes.Scan) {
                                    launchSingleTop = true
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 18.dp)
                                .offset(y = fabOffsetY)
                                .size(66.dp),
                            shape = CircleShape,
                            containerColor = Color.Black,
                            contentColor = Color.White
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                modifier = Modifier.size(29.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun OnboardingData.isCompleted(): Boolean {
    return name.isNotBlank() &&
        gradeId != null &&
        goalId != null &&
        learningMethodId != null &&
        studyTimeId != null &&
        languageId != null
}

@Composable
private fun ExpandableHomeFab(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onRecordAudioClick: () -> Unit,
    onUploadAudioClick: () -> Unit,
    onUploadDocumentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerWidth by animateDpAsState(
        targetValue = if (expanded) 286.dp else 66.dp,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 450f),
        label = "fab_expand_width"
    )
    val containerHeight by animateDpAsState(
        targetValue = if (expanded) 210.dp else 66.dp,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 450f),
        label = "fab_expand_height"
    )
    val cornerRadius by animateDpAsState(
        targetValue = if (expanded) 24.dp else 33.dp,
        animationSpec = spring(dampingRatio = 0.9f, stiffness = 500f),
        label = "fab_expand_corner"
    )
    val plusRotation by animateFloatAsState(
        targetValue = if (expanded) 45f else 0f,
        animationSpec = spring(dampingRatio = 0.9f, stiffness = 600f),
        label = "fab_plus_rotation"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .width(containerWidth)
                .height(containerHeight),
            color = Color(0xFF1F2024),
            shape = RoundedCornerShape(cornerRadius)
        ) {
            if (expanded) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 14.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .fillMaxWidth()
                    ) {
                        ExpandableFabActionRow(
                            title = "Audio Record",
                            icon = vectorResource(Res.drawable.ic_scan),
                            onClick = {
                                onExpandedChange(false)
                                onRecordAudioClick()
                            }
                        )
                        ExpandableFabActionRow(
                            title = "Audio Upload",
                            icon = vectorResource(Res.drawable.ic_stars),
                            onClick = {
                                onExpandedChange(false)
                                onUploadAudioClick()
                            }
                        )
                        ExpandableFabActionRow(
                            title = "Documents Upload",
                            icon = vectorResource(Res.drawable.ic_cards),
                            onClick = {
                                onExpandedChange(false)
                                onUploadDocumentClick()
                            }
                        )
                    }
                }
            } else {
                FloatingActionButton(
                    onClick = { onExpandedChange(true) },
                    modifier = Modifier.fillMaxSize(),
                    shape = CircleShape,
                    containerColor = Color.Black,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier
                            .size(29.dp)
                            .rotate(plusRotation)
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpandableFabActionRow(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 4.dp, end = 0.dp, top = 3.dp, bottom = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium
        )
        Surface(
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.14f),
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(19.dp)
                )
            }
        }
    }
}
