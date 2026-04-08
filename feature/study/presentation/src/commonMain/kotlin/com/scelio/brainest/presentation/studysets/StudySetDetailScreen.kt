package com.scelio.brainest.presentation.studysets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.presentation.components.StudySetItem
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.Instant as KxInstant

private const val DefaultGenerationCount = 10
private const val DefaultMultipleChoice = true

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

    LaunchedEffect(deckId) {
        viewModel.load(deckId)
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
                        .verticalScroll(rememberScrollState())
                        .padding(start = 16.dp, end = 16.dp, bottom = 20.dp),
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
                        },
                        windowInsets = WindowInsets(0, 0, 0, 0)
                    )
                    

                    StudySetItem(
                        title = deck.title,
                        createdAt = formatDate(deck.createdAt),
                        flashcardsCount = deck.totalCards,
                        quizCount = state.quizCount,
                        flashcardsSwiped = state.flashcardsSwiped,
                        quizzesCompleted = state.quizzesCompleted,
                        onClick = {}
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StudySetActionItem(
                            title = "Flashcards",
                            icon = Icons.Outlined.AutoStories,
                            enabled = !state.isGenerating,
                            onClick = { viewModel.generateFlashcards(DefaultGenerationCount) }
                        )
                        StudySetActionItem(
                            title = "Quiz",
                            icon = Icons.Outlined.Quiz,
                            enabled = !state.isGenerating,
                            onClick = { viewModel.generateQuiz(DefaultGenerationCount, DefaultMultipleChoice) }
                        )
                    }

                    SmartNotesSection(
                        notes = state.smartNotes,
                        isLoading = state.isSmartNotesLoading,
                        error = state.smartNotesError,
                        onRetry = viewModel::generateSmartNotes
                    )
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
            val generationLabel = when (state.generationTarget) {
                GenerationTarget.Flashcards -> "Generating flashcards"
                GenerationTarget.Quiz -> "Generating quiz"
                null -> "Generating"
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.7f))
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = generationLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun StudySetActionItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    Surface(
        color = colorScheme.surfaceVariant,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = enabled, onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SmartNotesSection(
    notes: String?,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Smart notes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (isLoading) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Generating smart notes...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            return
        }

        if (!notes.isNullOrBlank()) {
            SmartNotesContent(notes = notes)
            return
        }

        Text(
            text = "Smart notes will appear here once generated.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (!error.isNullOrBlank()) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }

        TextButton(onClick = onRetry) {
            Text(text = "Generate smart notes")
        }
    }
}

@Composable
private fun SmartNotesContent(
    notes: String,
    modifier: Modifier = Modifier
) {
    val sections = remember(notes) { parseSmartNotesSections(notes) }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        sections.forEach { section ->
            if (!section.heading.isNullOrBlank()) {
                Text(
                    text = section.heading,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            val bodyText = section.body
                .map { line -> normalizeSmartNotesLine(line) }
                .joinToString("\n")
                .trim()
            if (bodyText.isNotBlank()) {
                Text(
                    text = bodyText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private data class SmartNotesSectionData(
    val heading: String?,
    val body: List<String>
)

private fun parseSmartNotesSections(raw: String): List<SmartNotesSectionData> {
    val sections = mutableListOf<SmartNotesSectionData>()
    var heading: String? = null
    val body = mutableListOf<String>()

    fun flush() {
        if (heading != null || body.isNotEmpty()) {
            sections.add(SmartNotesSectionData(heading = heading, body = body.toList()))
        }
        heading = null
        body.clear()
    }

    raw.lineSequence().forEach { line ->
        val trimmed = line.trim()
        if (trimmed.startsWith("## ")) {
            flush()
            heading = trimmed.removePrefix("## ").trim()
        } else if (trimmed.isNotEmpty()) {
            body.add(trimmed)
        } else if (body.isNotEmpty()) {
            body.add("")
        }
    }
    flush()

    return if (sections.isNotEmpty()) sections else listOf(
        SmartNotesSectionData(heading = null, body = raw.lines())
    )
}

private fun normalizeSmartNotesLine(line: String): String {
    val trimmed = line.trim()
    return when {
        trimmed.startsWith("- ") -> "• ${trimmed.removePrefix("- ").trim()}"
        trimmed.startsWith("* ") -> "• ${trimmed.removePrefix("* ").trim()}"
        else -> line
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
