package com.scelio.brainest.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.scelio.brainest.presentation.chat_detail.ChatDetailScreen
import com.scelio.brainest.presentation.chat_detail.ChatDetailViewModel
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel


sealed interface ChatGraphRoutes {
    @Serializable
    data object Graph: ChatGraphRoutes

    @Serializable
    data object ChatListDetailRoute: ChatGraphRoutes
}

fun NavGraphBuilder.chatGraph(
    navController: NavController
) {
    navigation<ChatGraphRoutes.Graph>(
        startDestination = ChatGraphRoutes.ChatListDetailRoute
    ) {
        composable<ChatGraphRoutes.ChatListDetailRoute> {
            val viewModel = koinViewModel<ChatDetailViewModel>()
            ChatDetailScreen(
                viewModel = viewModel,
                chatId = TODO(),
                userId = TODO(),
                modifier = TODO()
            )
        }
    }
}