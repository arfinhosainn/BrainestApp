package com.scelio.brainest.presentation.flashcards

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.scelio.brainest.presentation.FlashCardScreen
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FlashcardsSessionScreen(
    deckId: String,
    viewModel: FlashcardsSessionViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    LaunchedEffect(deckId) {
        viewModel.loadDeck(deckId)
    }

    DisposableEffect(deckId) {
        onDispose {
            scope.launch { viewModel.finishSession() }
        }
    }

    FlashCardScreen(
        cards = state.cards,
        initialCurrentIndex = state.currentIndex,
        initialKnowCount = state.knownCount,
        initialDontKnowCount = state.unknownCount,
        error = state.error,
        onCardKnown = { viewModel.onCardKnown(it) },
        onCardUnknown = { viewModel.onCardUnknown(it) },
        onSessionFinished = { _, _, _ -> viewModel.finishSession() }
    )
}
