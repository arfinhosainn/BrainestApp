package com.scelio.brainest.presentation.studysets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import brainest.feature.study.presentation.generated.resources.Res
import brainest.feature.study.presentation.generated.resources.study_sets_add
import brainest.feature.study.presentation.generated.resources.study_sets_empty
import brainest.feature.study.presentation.generated.resources.study_sets_empty_hint
import com.scelio.brainest.designsystem.BricolageGrotesq
import com.scelio.brainest.presentation.components.StudySetItem
import com.scelio.brainest.presentation.components.UploadDocsBottomSheet
import com.scelio.brainest.presentation.flashcards.rememberDocumentPicker
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
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

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF00AD67))) {
        Column(modifier = Modifier.fillMaxSize()) {
            var selectedFilter by remember { mutableStateOf("All sets") }

            TopAppBar(
                title = {
                    Text(
                        text = "Library",
                        fontFamily = BricolageGrotesq,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                windowInsets = TopAppBarDefaults.windowInsets
            )

            // Filter chips row (horizontal, scrollable) - custom rounded chips with BricolageGrotesq font
            val chips = listOf("All sets", "Recent", "Mastered", "Not complete")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                chips.forEach { chip ->
                    val isSelected = selectedFilter == chip
                    Surface(
                        tonalElevation = 0.dp,
                        shape = RoundedCornerShape(24.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color(
                            0xFFE8F0E6
                        ),
                        modifier = Modifier
                            .clickable { selectedFilter = chip }
                            .padding(vertical = 6.dp)
                    ) {
                        Text(
                            text = chip,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = BricolageGrotesq
                        )
                    }
                }
            }

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
                        // Precompute recent set ids (top 10 by createdAt). This avoids needing platform clocks in commonMain.
                        val recentIds = state.sets
                            .sortedByDescending {
                                runCatching { it.createdAt.toEpochMilliseconds() }.getOrNull()
                                    ?: Long.MIN_VALUE
                            }
                            .take(10)
                            .map { it.id }
                            .toSet()

                        val filtered = state.sets.filter { set ->
                            val matchesFilter = when (selectedFilter) {
                                "All sets" -> true
                                "Recent" -> recentIds.contains(set.id)
                                "Mastered" -> {
                                    val fc = set.flashcardsCount
                                    val swiped = set.flashcardsSwiped
                                    fc in 1..swiped
                                }

                                "Not complete" -> {
                                    val fc = set.flashcardsCount
                                    val swiped = set.flashcardsSwiped
                                    val qc = set.quizCount
                                    val qDone = set.quizzesCompleted
                                    (fc > 0 && swiped < fc) || (qc > 0 && qDone < qc)
                                }

                                else -> true
                            }

                            matchesFilter
                        }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            contentPadding = PaddingValues(
                                top = 20.dp,
                                bottom = WindowInsets.navigationBars
                                    .asPaddingValues()
                                    .calculateBottomPadding() + 132.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            items(filtered, key = { it.id }) { set ->
                                val docType = resolveDocType(set.sourceFilename)

                                StudySetItem(
                                    id = set.id,
                                    title = set.title,
                                    createdAt = formatDate(set.createdAt),
                                    flashcardsCount = set.flashcardsCount,
                                    quizCount = set.quizCount,
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
                .padding(end = 24.dp, bottom = 27.dp),
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
    timeZone: TimeZone = TimeZone.UTC
): String {
    val localDate = Instant
        .fromEpochMilliseconds(instant.toEpochMilliseconds())
        .toLocalDateTime(timeZone)
        .date
    val month = monthNames[localDate.month.number - 1]
    return "$month ${localDate.day}, ${localDate.year}"
}

private val monthNames = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
)

private val documentExtensions = setOf(
    "pdf", "doc", "docx", "txt", "md", "rtf", "odt"
)

private val audioExtensions = setOf(
    "mp3", "wav", "m4a", "aac", "ogg", "flac", "opus", "webm"
)

private val visualOrSlideExtensions = setOf(
    "ppt", "pptx", "key", "png", "jpg", "jpeg", "webp", "gif", "bmp", "heic", "heif", "avif", "tiff"
)

private fun resolveDocType(sourceFilename: String?): com.scelio.brainest.presentation.components.DocType {
    val extension = sourceFilename
        ?.substringAfterLast('.', missingDelimiterValue = "")
        ?.lowercase()
        ?.trim()
        .orEmpty()

    return when {
        extension in audioExtensions -> com.scelio.brainest.presentation.components.DocType.AUDIO
        extension in documentExtensions -> com.scelio.brainest.presentation.components.DocType.DOCUMENT
        extension in visualOrSlideExtensions -> com.scelio.brainest.presentation.components.DocType.OTHER
        else -> com.scelio.brainest.presentation.components.DocType.OTHER
    }
}
