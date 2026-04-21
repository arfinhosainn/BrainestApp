package com.scelio.brainest.presentation.studysets

// ...existing code...
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import brainest.feature.study.presentation.generated.resources.Res
import brainest.feature.study.presentation.generated.resources.study_sets_add
import brainest.feature.study.presentation.generated.resources.study_sets_empty
import brainest.feature.study.presentation.generated.resources.study_sets_empty_hint
import brainest.feature.study.presentation.generated.resources.study_sets_title
import com.scelio.brainest.presentation.components.StudySetItem
import com.scelio.brainest.presentation.components.StudySetsSearchBar
import com.scelio.brainest.presentation.components.TopAppBarStudySets
import com.scelio.brainest.presentation.components.UploadDocsBottomSheet
import com.scelio.brainest.presentation.flashcards.rememberDocumentPicker
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Instant

@Composable
fun StudySetsScreen(
    onOpenSet: (String) -> Unit,
    onCreateSet: (String, Boolean) -> Unit,
    onRecordAudio: () -> Unit,
    viewModel: StudySetsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    var isUploadSheetVisible by remember { mutableStateOf(false) }

    val documentPicker = rememberDocumentPicker(
        onDocumentPicked = { document ->
            isUploadSheetVisible = false
            viewModel.createSetFromDocument(document)
        },
        onError = { message ->
            viewModel.setCreationError(message)
        }
    )

    LaunchedEffect(Unit) {
        viewModel.loadSets()
    }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.loadSets()
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is StudySetsEvent.OpenSetDetail -> {
                    onCreateSet(event.deckId, event.promptGeneration)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFEBFCF6))) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBarStudySets(title = stringResource(Res.string.study_sets_title))

            var query by remember { mutableStateOf("") }
            StudySetsSearchBar(
                initialQuery = "",
                placeholder = stringResource(Res.string.study_sets_title),
                onQueryChanged = { query = it }
            )

            Box(modifier = Modifier.weight(1f)) {
                when {
                    state.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    state.error != null -> {
                        Text(
                            text = state.error.orEmpty(),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 24.dp)
                        )
                    }

                    state.sets.isEmpty() -> {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(Res.string.study_sets_empty),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(Res.string.study_sets_empty_hint),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    else -> {
                        val filtered = if (query.isBlank()) state.sets else state.sets.filter { it.title.contains(query, ignoreCase = true) }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(filtered, key = { it.id }) { set ->
                                val filename = set.sourceFilename
                                val lower = filename?.lowercase()
                                val docType = if (lower != null && (lower.endsWith(".mp3") || lower.endsWith(".wav") || lower.endsWith(".m4a") || lower.endsWith(".aac") || lower.endsWith(".ogg"))) {
                                    com.scelio.brainest.presentation.components.DocType.AUDIO
                                } else {
                                    com.scelio.brainest.presentation.components.DocType.DOCUMENT
                                }

                                StudySetItem(
                                    id = set.id,
                                    title = set.title,
                                    createdAt = formatDate(set.createdAt),
                                    flashcardsCount = set.flashcardsCount,
                                    quizCount = set.quizCount,
                                    flashcardsSwiped = set.flashcardsSwiped,
                                    quizzesCompleted = set.quizzesCompleted,
                                    masteryPercent = 0,
                                    docType = docType,
                                    onSetClick = { id -> onOpenSet(id) }
                                )
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { isUploadSheetVisible = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(Res.string.study_sets_add)
            )
        }

        if (state.isCreating) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        if (state.creationError != null) {
            Text(
                text = state.creationError.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp)
            )
        }
    }

    if (isUploadSheetVisible) {
        UploadDocsBottomSheet(
            onDismiss = { isUploadSheetVisible = false },
            onRecordAudio = {
                isUploadSheetVisible = false
                onRecordAudio()
            },
            onUploadDocument = {
                documentPicker.launch()
            }
        )
    }
}

private fun formatDate(
    instant: Instant,
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): String {
    val localDate = Instant
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
