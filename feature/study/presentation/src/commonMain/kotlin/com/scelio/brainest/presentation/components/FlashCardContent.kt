package com.scelio.brainest.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import brainest.feature.study.presentation.generated.resources.Res
import brainest.feature.study.presentation.generated.resources.flashcard_answer_format
import brainest.feature.study.presentation.generated.resources.flashcard_hint
import brainest.feature.study.presentation.generated.resources.flashcard_question_format
import com.scelio.brainest.flashcards.domain.Flashcard
import org.jetbrains.compose.resources.stringResource

@Composable
fun FlashCardContent(
    card: Flashcard,
    cardIndex: Int,
    isInteractive: Boolean = true
) {
    var face by remember(cardIndex) { mutableStateOf(CardFace.Front) }

    FlipCard(
        face = face,
        onClick = {
            if (isInteractive) {
                face = if (face == CardFace.Front) CardFace.Back else CardFace.Front
            }
        },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(380.dp),

        front = {
            Card(
                modifier = Modifier.fillMaxSize(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(Res.string.flashcard_question_format, cardIndex + 1),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = card.front,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        if (isInteractive) {
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = stringResource(Res.string.flashcard_hint),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        },

        back = {
            Card(
                modifier = Modifier.fillMaxSize(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(Res.string.flashcard_answer_format, cardIndex + 1),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = card.back,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        if (isInteractive) {
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = stringResource(Res.string.flashcard_hint),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    )
}
