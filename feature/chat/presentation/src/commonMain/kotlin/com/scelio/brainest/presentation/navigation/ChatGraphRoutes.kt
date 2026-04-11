package com.scelio.brainest.presentation.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.scelio.brainest.domain.auth.AuthService
import com.scelio.brainest.presentation.chat_detail.ChatDetailScreen
import com.scelio.brainest.presentation.chat_detail.ChatDetailViewModel
import com.scelio.brainest.presentation.chat_list.ChatListRoot
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel


sealed interface ChatGraphRoutes {
    @Serializable data object Graph : ChatGraphRoutes
    @Serializable data object ChatListRoute : ChatGraphRoutes
    @Serializable data class ChatDetailRoute(val chatId: String? = null) : ChatGraphRoutes
}

fun NavGraphBuilder.chatGraph(navController: NavController) {
    navigation<ChatGraphRoutes.Graph>(
        startDestination = ChatGraphRoutes.ChatDetailRoute()
    ) {
        composable<ChatGraphRoutes.ChatListRoute> {
            ChatListRoot(
                onNavigateToChat = { chatId ->
                    navController.navigate(ChatGraphRoutes.ChatDetailRoute(chatId))
                }
            )
        }

        composable<ChatGraphRoutes.ChatDetailRoute>(
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(
                        durationMillis = 950,
                        easing = FastOutSlowInEasing
                    )
                )
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(
                        durationMillis = 540,
                        easing = FastOutSlowInEasing
                    )
                )
            },
            popExitTransition = {
                slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(
                        durationMillis = 540,
                        easing = FastOutSlowInEasing
                    )
                )
            }
        ) { backStackEntry ->
            val viewModel = koinViewModel<ChatDetailViewModel>()
            val authService = org.koin.compose.koinInject<AuthService>()
            val args = backStackEntry.toRoute<ChatGraphRoutes.ChatDetailRoute>()

            var userId by remember { mutableStateOf<String?>(null) }
            LaunchedEffect(Unit) { userId = authService.currentUserId() }

            userId?.let { uid ->
                ChatDetailScreen(
                    viewModel = viewModel,
                    chatId = args.chatId,
                    userId = uid,
                    onNewChatClick = {
                        navController.navigate(ChatGraphRoutes.ChatDetailRoute()) {
                            launchSingleTop = true
                            popUpTo(ChatGraphRoutes.Graph) {
                                inclusive = false
                            }
                        }
                    },
                    onOpenChatsClick = {
                        navController.navigate(ChatGraphRoutes.ChatListRoute) {
                            launchSingleTop = true
                            popUpTo(ChatGraphRoutes.Graph) {
                                inclusive = false
                            }
                        }
                    },
                    onNavigateToChat = { selectedChatId ->
                        navController.navigate(ChatGraphRoutes.ChatDetailRoute(selectedChatId)) {
                            launchSingleTop = true
                            popUpTo(ChatGraphRoutes.Graph) {
                                inclusive = false
                            }
                        }
                    },
                    onCloseClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
