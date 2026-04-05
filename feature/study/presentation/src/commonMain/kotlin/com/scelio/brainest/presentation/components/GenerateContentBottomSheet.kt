package com.scelio.brainest.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateContentBottomSheet(
    countText: String,
    onCountChange: (String) -> Unit,
    multipleChoice: Boolean,
    onMultipleChoiceChange: (Boolean) -> Unit,
    showQuizToggle: Boolean,
    showFlashcards: Boolean,
    showQuiz: Boolean,
    onGenerateFlashcards: () -> Unit,
    onGenerateQuiz: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Generate content",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = countText,
                onValueChange = onCountChange,
                label = { Text("Number of questions/cards") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            if (showQuizToggle) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Multiple choice (4 options)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = multipleChoice,
                        onCheckedChange = onMultipleChoiceChange
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (showFlashcards) {
                    Button(
                        onClick = onGenerateFlashcards,
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("Flashcards")
                    }
                }
                if (showQuiz) {
                    Button(
                        onClick = onGenerateQuiz,
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("Quiz")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
