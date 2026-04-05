package com.scelio.brainest.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.scelio.brainest.presentation.audio.AudioRecordingScreen
import com.scelio.brainest.presentation.flashcards.FlashcardsSessionScreen
import com.scelio.brainest.presentation.quiz.QuizSessionScreen
import com.scelio.brainest.presentation.studysets.StudySetDetailScreen
import com.scelio.brainest.presentation.studysets.StudySetsScreen

fun NavGraphBuilder.flashcardsGraph(
    navController: NavController
) {
    navigation<FlashcardsGraphRoutes.Graph>(
        startDestination = FlashcardsGraphRoutes.Generate
    ) {
        composable<FlashcardsGraphRoutes.Generate> {
            StudySetsScreen(
                onOpenSet = { deckId ->
                    navController.navigate(FlashcardsGraphRoutes.StudySetDetail(deckId))
                },
                onCreateSet = { deckId, promptGeneration ->
                    navController.navigate(FlashcardsGraphRoutes.StudySetDetail(deckId, promptGeneration))
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

        composable<FlashcardsGraphRoutes.StudySetDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<FlashcardsGraphRoutes.StudySetDetail>()
            StudySetDetailScreen(
                deckId = route.deckId,
                promptGeneration = route.promptGeneration,
                onBackClick = { navController.popBackStack() },
                onOpenFlashcards = { deckId ->
                    navController.navigate(FlashcardsGraphRoutes.Session(deckId))
                },
                onOpenQuiz = { deckId ->
                    navController.navigate(FlashcardsGraphRoutes.QuizSession(deckId))
                }
            )
        }

        composable<FlashcardsGraphRoutes.QuizSession> { backStackEntry ->
            val route = backStackEntry.toRoute<FlashcardsGraphRoutes.QuizSession>()
            QuizSessionScreen(
                deckId = route.deckId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<FlashcardsGraphRoutes.AudioRecording> {
            AudioRecordingScreen(
                onBackClick = { navController.popBackStack() },
                onStudySetReady = { deckId ->
                    navController.navigate(
                        FlashcardsGraphRoutes.StudySetDetail(
                            deckId = deckId,
                            promptGeneration = true
                        )
                    )
                }
            )
        }
    }
}
