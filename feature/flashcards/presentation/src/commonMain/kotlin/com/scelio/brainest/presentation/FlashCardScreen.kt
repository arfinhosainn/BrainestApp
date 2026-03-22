package com.scelio.brainest.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.Typography
import com.scelio.brainest.designsystem.components.buttons.BrainestButton
import com.scelio.brainest.designsystem.components.buttons.BrainestButtonStyle
import com.scelio.brainest.presentation.components.FlashCardContent
import com.scelio.brainest.presentation.components.FlashCardCounters
import com.scelio.brainest.presentation.components.SwipeCard
import org.jetbrains.compose.ui.tooling.preview.Preview


private val ColorKnowBackground = Color(0xFF33CA67)
private val ColorKnowAccent = Color(0xFFFFFFFF)
private val ColorDontKnowBackground = Color(0xFFF8D7DA)
private val ColorDontKnowAccent = Color(0xFFF8D7DA)
private val ColorKnowButton = Color(0xFF33CA67)
private val ColorDontKnowButton = Color(0xFFE8E8E8)

data class FlashCard(
    val question: String,
    val answer: String
)

@Composable
fun FlashCardScreen(
    cards: List<FlashCard>,
    error: String?,
    onRetry: () -> Unit = {}
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var knowCount by remember { mutableIntStateOf(0) }
    var dontKnowCount by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {

        // ── "X of Y cards reviewed" ───────────────────────────────────────
        if (cards.isNotEmpty()) {
            Text(
                text = "$currentIndex of ${cards.size} cards reviewed",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Spacer(Modifier.height(16.dp))

        when {
            error != null -> {
                // TODO: show your error UI
            }

            cards.isEmpty() -> {
                // TODO: show your empty state UI
            }

            currentIndex >= cards.size -> {
                // TODO: show your "all done" UI + restart button
            }

            else -> {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    for (i in 2 downTo 0) {
                        val cardIndex = currentIndex + i
                        if (cardIndex < cards.size) {
                            val isTopCard = i == 0

                            Box(
                                modifier = Modifier.graphicsLayer {
                                    scaleX = 1f - (i * 0.05f)
                                    scaleY = 1f - (i * 0.05f)
                                    translationY = i * 20f
                                    alpha = 1f - (i * 0.2f)
                                }
                            ) {
                                if (isTopCard) {
                                    SwipeCard(
                                        // Swipe LEFT  = Don't Know
                                        onSwipeLeft = {
                                            dontKnowCount++
                                            if (currentIndex < cards.size) currentIndex++
                                        },
                                        // Swipe RIGHT = Know
                                        onSwipeRight = {
                                            knowCount++
                                            if (currentIndex < cards.size) currentIndex++
                                        }
                                    ) {
                                        FlashCardContent(
                                            card = cards[cardIndex],
                                            cardIndex = cardIndex,
                                            isInteractive = true
                                        )
                                    }
                                } else {
                                    FlashCardContent(
                                        card = cards[cardIndex],
                                        cardIndex = cardIndex,
                                        isInteractive = false
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FlashCardCounters(
                        count = dontKnowCount,
                        label = "DON'T KNOW",
                        backgroundColor = Color.Transparent,
                        countColor = Color.Black,
                        labelColor = ColorDontKnowAccent,
                        modifier = Modifier.weight(1f)
                    )
                    FlashCardCounters(
                        count = knowCount,
                        label = "KNOW",
                        backgroundColor = Color.Transparent,
                        countColor = Color.Black,
                        labelColor = ColorKnowAccent,
                        modifier = Modifier.weight(1f)
                    )

                    BrainestButton(
                        text = "",
                        onClick = {},
                        modifier = Modifier.weight(1f).height(60.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "",
                                modifier = Modifier.size(20.dp),
                            )
                        },
                        textStyles = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Typography.bodyMedium.fontFamily,
                        ),
                        style = BrainestButtonStyle.PRIMARY
                    )

                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BrainestButton(
                        text = "Don't Know",
                        onClick = {},
                        modifier = Modifier.weight(1f).height(60.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "",
                                modifier = Modifier.size(20.dp),
                            )
                        },
                        textStyles = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Typography.bodyMedium.fontFamily,
                        ),
                        style = BrainestButtonStyle.PRIMARY
                    )


                    BrainestButton(
                        text = "Know",
                        onClick = {},
                        modifier = Modifier.weight(1f).height(60.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "",
                                modifier = Modifier.size(20.dp),
                            )
                        },
                        textStyles = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Typography.bodyMedium.fontFamily,
                        ),
                        style = BrainestButtonStyle.PRIMARY
                    )
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

private val sampleCards = listOf(
    FlashCard("What is the capital of France?", "Paris"),
    FlashCard("What is 2 + 2?", "4"),
    FlashCard("Who wrote Romeo and Juliet?", "William Shakespeare"),
    FlashCard("What is the speed of light?", "299,792,458 m/s"),
    FlashCard("What is H2O?", "Water"),
)

@Preview(name = "Full Screen", showBackground = true)
@Composable
private fun PreviewFlashCardScreenFull() {
    BrainestTheme {
        Surface(color = Color.White) {
            FlashCardScreen(cards = sampleCards, error = null)
        }
    }
}