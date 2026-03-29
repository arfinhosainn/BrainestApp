package com.scelio.brainest.presentation.flashcards

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scelio.brainest.presentation.components.UploadDocsBottomSheet
import org.koin.compose.viewmodel.koinViewModel

@Suppress("UNUSED_PARAMETER")
@Composable
fun FlashcardsGenerateScreen(
    onStartReview: (String) -> Unit,
    viewModel: FlashcardsGenerateViewModel = koinViewModel()
) {
    var isUploadSheetVisible by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
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
                contentDescription = "Upload documents"
            )
        }
    }

    if (isUploadSheetVisible) {
        UploadDocsBottomSheet(onDismiss = { isUploadSheetVisible = false })
    }
}

