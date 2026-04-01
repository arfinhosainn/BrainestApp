package com.scelio.brainest.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.scelio.brainest.presentation.audio.AudioRecordingScreen
import com.scelio.brainest.presentation.flashcards.FlashcardsGenerateScreen
import com.scelio.brainest.presentation.flashcards.FlashcardsSessionScreen

fun NavGraphBuilder.flashcardsGraph(
    navController: NavController
) {
    navigation<FlashcardsGraphRoutes.Graph>(
        startDestination = FlashcardsGraphRoutes.Generate
    ) {
        composable<FlashcardsGraphRoutes.Generate> {
            FlashcardsGenerateScreen(
                onStartReview = { deckId ->
                    navController.navigate(FlashcardsGraphRoutes.Session(deckId))
                },
                onRecordAudio = {
                    navController.navigate(FlashcardsGraphRoutes.AudioRecording)
                }
            )
        }

        composable<FlashcardsGraphRoutes.Session> { backStackEntry ->
            val route = backStackEntry.toRoute<FlashcardsGraphRoutes.Session>()
            FlashcardsSessionScreen(deckId = route.deckId)
        }

        composable<FlashcardsGraphRoutes.AudioRecording> {
            AudioRecordingScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
