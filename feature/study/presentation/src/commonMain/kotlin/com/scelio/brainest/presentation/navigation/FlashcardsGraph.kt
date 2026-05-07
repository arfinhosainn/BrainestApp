package com.scelio.brainest.presentation.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.navigation.NavController
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.scelio.brainest.presentation.audio.AudioRecordingScreen
import com.scelio.brainest.presentation.flashcards.FlashcardsSessionScreen
import com.scelio.brainest.presentation.quiz.QuizSessionScreen
import com.scelio.brainest.presentation.studysets.StudySetDetailScreen
import com.scelio.brainest.presentation.studysets.StudySetsScreen

private const val StudySetEnterDurationMs = 400
private const val StudySetExitDurationMs = 300
private val StudySetDepthSpring =
    spring<Float>(dampingRatio = 0.85f, stiffness = Spring.StiffnessLow)
private const val StudySetInitialScale = 0.92f
private const val StudySetDetailRouteSuffix = ".StudySetDetail"

fun NavGraphBuilder.flashcardsGraph(
    navController: NavController
) {
    navigation<FlashcardsGraphRoutes.Graph>(
        startDestination = FlashcardsGraphRoutes.Generate
    ) {
        composable<FlashcardsGraphRoutes.Generate>(
            exitTransition = {
                if (targetState.isStudySetDetailRoute()) {
                    fadeOut(
                        animationSpec = tween(
                            durationMillis = StudySetExitDurationMs,
                            easing = FastOutSlowInEasing
                        )
                    ) + scaleOut(
                        targetScale = StudySetInitialScale,
                        animationSpec = StudySetDepthSpring
                    )
                } else {
                    null
                }
            },
            popEnterTransition = {
                if (initialState.isStudySetDetailRoute()) {
                    fadeIn(
                        animationSpec = tween(
                            durationMillis = StudySetEnterDurationMs,
                            easing = FastOutSlowInEasing
                        )
                    ) + scaleIn(
                        initialScale = StudySetInitialScale,
                        animationSpec = StudySetDepthSpring
                    )
                } else {
                    null
                }
            }
        ) {
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

        composable<FlashcardsGraphRoutes.StudySetDetail>(
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        durationMillis = StudySetEnterDurationMs,
                        easing = FastOutSlowInEasing
                    )
                ) + scaleIn(
                    initialScale = StudySetInitialScale,
                    animationSpec = StudySetDepthSpring
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        durationMillis = StudySetExitDurationMs,
                        easing = FastOutSlowInEasing
                    )
                ) + scaleOut(
                    targetScale = StudySetInitialScale,
                    animationSpec = StudySetDepthSpring
                )
            },
            popExitTransition = {
                fadeOut(
                    animationSpec = tween(
                        durationMillis = StudySetExitDurationMs,
                        easing = FastOutSlowInEasing
                    )
                ) + scaleOut(
                    targetScale = StudySetInitialScale,
                    animationSpec = StudySetDepthSpring
                )
            }
        ) { backStackEntry ->
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

private fun NavBackStackEntry.isStudySetDetailRoute(): Boolean {
    val route = destination.route.orEmpty()
    return route.endsWith(StudySetDetailRouteSuffix) || route.contains(StudySetDetailRouteSuffix)
}
