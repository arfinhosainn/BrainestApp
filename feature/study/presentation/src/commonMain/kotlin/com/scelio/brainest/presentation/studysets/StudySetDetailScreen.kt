package com.scelio.brainest.presentation.studysets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.presentation.components.GenerateContentBottomSheet
import com.scelio.brainest.presentation.components.StudySetItem
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.Instant as KxInstant

private enum class GenerationSheetMode {
    All,
    FlashcardsOnly,
    QuizOnly
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudySetDetailScreen(
    deckId: String,
    promptGeneration: Boolean,
    onBackClick: () -> Unit,
    onOpenFlashcards: (String) -> Unit,
    onOpenQuiz: (String) -> Unit,
    viewModel: StudySetDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var generationMode by remember { mutableStateOf<GenerationSheetMode?>(null) }
    var countText by remember { mutableStateOf("10") }
    var multipleChoice by remember { mutableStateOf(true) }

    LaunchedEffect(deckId) {
        viewModel.load(deckId)
    }

    LaunchedEffect(promptGeneration, state.deck) {
        if (promptGeneration && state.deck != null) {
            generationMode = GenerationSheetMode.All
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is StudySetDetailEvent.OpenFlashcards -> onOpenFlashcards(event.deckId)
                is StudySetDetailEvent.OpenQuiz -> onOpenQuiz(event.deckId)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            state.deck != null -> {
                val deck = requireNotNull(state.deck)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TopAppBar(
                        title = { Text(text = "Study set") },
                        navigationIcon = {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    )
                    Text(
                        text = deck.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    StudySetItem(
                        title = deck.title,
                        createdAt = formatDate(deck.createdAt),
                        flashcardsCount = deck.totalCards,
                        quizCount = state.quizCount,
                        onClick = {}
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Flashcards",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (deck.totalCards > 0) {
                        Text(
                            text = "Open existing flashcards.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        GenerateActionButton(
                            text = "Open Flashcards",
                            onClick = { viewModel.openFlashcards() }
                        )
                    } else {
                        Text(
                            text = "Generate flashcards from this study set.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        GenerateActionButton(
                            text = "Generate Flashcards",
                            onClick = {
                                generationMode = GenerationSheetMode.FlashcardsOnly
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Quiz",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (state.quizCount > 0) {
                        Text(
                            text = "Open existing quiz.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        GenerateActionButton(
                            text = "Open Quiz",
                            onClick = { viewModel.openQuiz() }
                        )
                    } else {
                        Text(
                            text = "Generate a quiz from this study set.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        GenerateActionButton(
                            text = "Generate Quiz",
                            onClick = {
                                generationMode = GenerationSheetMode.QuizOnly
                            }
                        )
                    }
                }
            }
        }

        if (state.error != null) {
            Text(
                text = state.error.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp)
            )
        }

        if (state.isGenerating) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }

    val mode = generationMode
    if (mode != null) {
        GenerateContentBottomSheet(
            countText = countText,
            onCountChange = { countText = it },
            multipleChoice = multipleChoice,
            onMultipleChoiceChange = { multipleChoice = it },
            showQuizToggle = mode != GenerationSheetMode.FlashcardsOnly,
            showFlashcards = mode != GenerationSheetMode.QuizOnly,
            showQuiz = mode != GenerationSheetMode.FlashcardsOnly,
            onGenerateFlashcards = {
                val count = countText.toIntOrNull()?.coerceIn(3, 50) ?: 10
                generationMode = null
                viewModel.generateFlashcards(count)
            },
            onGenerateQuiz = {
                val count = countText.toIntOrNull()?.coerceIn(3, 50) ?: 10
                generationMode = null
                viewModel.generateQuiz(count, multipleChoice)
            },
            onDismiss = { generationMode = null }
        )
    }
}

@Composable
private fun GenerateActionButton(
    text: String,
    onClick: () -> Unit
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(48.dp)
    ) {
        Text(text)
    }
}

@Preview
@Composable
private fun PreviewStudySetDetailScreen() {
    BrainestTheme {
        Surface {
            StudySetDetailScreen(
                deckId = "preview",
                promptGeneration = false,
                onBackClick = {},
                onOpenFlashcards = {},
                onOpenQuiz = {}
            )
        }
    }
}

private fun formatDate(
    instant: kotlin.time.Instant,
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): String {
    val localDate = KxInstant
        .fromEpochMilliseconds(instant.toEpochMilliseconds())
        .toLocalDateTime(timeZone)
        .date
    val month = monthNames[localDate.monthNumber - 1]
    return "$month ${localDate.dayOfMonth}, ${localDate.year}"
}

private val monthNames = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
)
