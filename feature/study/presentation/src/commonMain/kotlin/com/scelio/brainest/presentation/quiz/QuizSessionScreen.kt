package com.scelio.brainest.presentation.quiz

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel
import com.scelio.brainest.presentation.quiz.components.QuizOptionState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import com.scelio.brainest.presentation.quiz.QuizResultsScreen

@Composable
fun QuizSessionScreen(
    deckId: String,
    onBackClick: () -> Unit,
    viewModel: QuizSessionViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(deckId) {
        viewModel.load(deckId)
    }

    val question = state.questions.getOrNull(state.currentIndex)
    val totalQuestions = state.questions.size

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Loading quiz...", style = MaterialTheme.typography.bodyMedium)
        }
        return
    }

    if (state.error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = state.error.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
        return
    }

    if (state.isCompleted) {
        QuizResultsScreen(
            totalQuestions = totalQuestions,
            answeredQuestions = state.answeredQuestions,
            correctAnswers = state.correctAnswers,
            onBackClick = onBackClick,
            onRetryClick = viewModel::restartQuiz,
            modifier = Modifier
        )
        return
    }

    if (question != null) {
        val selectedIndex = state.selectedAnswers[question.id]
        val options = remember(question.id, selectedIndex) {
            question.options.mapIndexed { index, text ->
                val optionState = when {
                    selectedIndex == null -> QuizOptionState.Default
                    index == question.correctIndex -> QuizOptionState.Correct
                    index == selectedIndex -> QuizOptionState.Incorrect
                    else -> QuizOptionState.Default
                }
                QuizOptionUi(
                    id = index.toString(),
                    text = text,
                    state = optionState
                )
            }
        }

        QuizScreen(
            title = "Quiz",
            questionIndex = state.currentIndex + 1,
            totalQuestions = totalQuestions,
            question = question.question,
            options = options,
            timeLeftText = "05:42",
            totalTimeText = "10:00",
            progress = if (totalQuestions == 0) 0f else (state.currentIndex + 1).toFloat() / totalQuestions,
            hintCount = 0,
            onBackClick = onBackClick,
            onOptionClick = { id ->
                viewModel.selectOption(id.toIntOrNull() ?: 0)
            },
            onPreviousClick = { viewModel.goPrevious() },
            onNextClick = { viewModel.goNext() },
            onHintClick = {},
            optionsEnabled = selectedIndex == null,
            modifier = Modifier
        )
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "No quiz questions available.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
