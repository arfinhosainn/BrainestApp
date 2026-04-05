package com.scelio.brainest.presentation.studysets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scelio.brainest.presentation.components.StudySetItem
import com.scelio.brainest.presentation.components.UploadDocsBottomSheet
import com.scelio.brainest.presentation.flashcards.rememberDocumentPicker
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Instant
import kotlinx.datetime.Instant as KxInstant

@Composable
fun StudySetsScreen(
    onOpenSet: (String) -> Unit,
    onCreateSet: (String, Boolean) -> Unit,
    onRecordAudio: () -> Unit,
    viewModel: StudySetsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
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

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is StudySetsEvent.OpenSetDetail -> {
                    onCreateSet(event.deckId, event.promptGeneration)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                        text = "No study sets yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap + to upload or record your first set.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.sets, key = { it.id }) { set ->
                        StudySetItem(
                            title = set.title,
                            createdAt = formatDate(set.createdAt),
                            flashcardsCount = set.flashcardsCount,
                            quizCount = set.quizCount,
                            onClick = { onOpenSet(set.id) }
                        )
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
                contentDescription = "Add study set"
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
