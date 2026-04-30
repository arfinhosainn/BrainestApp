package com.scellio.brainest

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scelio.brainest.domain.onboarding.OnboardingData
import com.scelio.brainest.domain.onboarding.OnboardingStore
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.brainest.presentation.navigation.OnboardingGraphRoutes
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.presentation.navigation.AuthGraphRoutes
import com.scelio.brainest.presentation.navigation.ChatGraphRoutes
import com.scelio.brainest.presentation.navigation.FlashcardsGraphRoutes
import com.scelio.brainest.presentation.navigation.HomeGraphRoutes
import com.scelio.brainest.presentation.navigation.ScanGraphRoutes
import com.scelio.brainest.presentation.util.ObserveAsEvents
import com.scellio.brainest.navigation.BrainestBottomNavigationBar
import com.scellio.brainest.navigation.DeepLinkListener
import com.scellio.brainest.navigation.NavigationRoot
import com.scellio.brainest.localization.AppLanguageState
import com.scellio.brainest.localization.applyAppLanguage
import com.scellio.brainest.localization.languageIdToTag
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
    val fabOffsetY = if (isIosPlatform()) (-15).dp else (-6).dp
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
            val showBottomBar = (state.isLoggedIn || isMainGraphRoute) && !hideBottomBar
            Scaffold(
                containerColor = Color.Transparent,
                bottomBar = {
                    if (showBottomBar) {
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

                            FloatingActionButton(
                                onClick = {
                                    navController.navigate(ScanGraphRoutes.Scan) {
                                        launchSingleTop = true
                                    }
                                },
                                modifier = Modifier
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
            ) { innerPadding ->
                val layoutDirection = LocalLayoutDirection.current
                val navHostPadding = if (isChatDetailRoute || isQuizRoute || isChatListRoute) {
                    PaddingValues(
                        start = innerPadding.calculateStartPadding(layoutDirection),
                        end = innerPadding.calculateEndPadding(layoutDirection),
                        bottom = 0.dp
                    )
                } else {
                    PaddingValues(
                        start = innerPadding.calculateStartPadding(layoutDirection),
                        end = innerPadding.calculateEndPadding(layoutDirection),
                        bottom = 0.dp
                    )
                }
                NavigationRoot(
                    navController = navController,
                    startDestination = startDestination,
                    modifier = Modifier.padding(navHostPadding)
                )
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
