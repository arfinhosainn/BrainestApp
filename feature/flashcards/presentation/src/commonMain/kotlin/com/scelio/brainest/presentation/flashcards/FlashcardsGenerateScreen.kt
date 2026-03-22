package com.scelio.brainest.presentation.flashcards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.scelio.brainest.designsystem.components.buttons.BrainestButton
import com.scelio.brainest.designsystem.components.buttons.BrainestButtonStyle
import com.scelio.brainest.designsystem.components.textfields.BrainestMultiLineTextField
import com.scelio.brainest.designsystem.components.textfields.BrainestTextField
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FlashcardsGenerateScreen(
    onStartReview: (String) -> Unit,
    viewModel: FlashcardsGenerateViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Generate Flashcards",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "Describe the topic and we will generate flashcards with GPT-4.",
            style = MaterialTheme.typography.bodyMedium
        )

        BrainestTextField(
            state = state.titleState,
            title = "Deck title",
            placeholder = "e.g. Biology: Cellular Respiration",
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        BrainestTextField(
            state = state.countState,
            title = "Number of cards",
            placeholder = "10",
            singleLine = true,
            keyboardType = KeyboardType.Number,
            modifier = Modifier.fillMaxWidth()
        )

        BrainestMultiLineTextField(
            state = state.promptState,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            placeholder = "Enter the topic or paste notes to generate flashcards."
        )

        BrainestButton(
            text = if (state.isGenerating) "Generating..." else "Generate & Save",
            onClick = { viewModel.generateAndSave() },
            enabled = !state.isGenerating,
            modifier = Modifier.fillMaxWidth()
        )

        state.lastDeckId?.let { deckId ->
            BrainestButton(
                text = "Start Review",
                onClick = { onStartReview(deckId) },
                style = BrainestButtonStyle.SECONDARY,
                modifier = Modifier.fillMaxWidth()
            )
        }

        state.message?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        state.error?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
