package com.scelio.brainest.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.presentation.components.CardFace
import com.scelio.brainest.presentation.components.FlipCard
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.roundToInt

// ---------------------------------------------------------------------------
// Colors
// ---------------------------------------------------------------------------

private val ColorKnowBackground     = Color(0xFFE8FAE8)
private val ColorKnowAccent         = Color(0xFF4CAF50)
private val ColorDontKnowBackground = Color(0xFFF2F2F2)
private val ColorDontKnowAccent     = Color(0xFF9E9E9E)
private val ColorKnowButton         = Color(0xFF66E060)
private val ColorDontKnowButton     = Color(0xFFE8E8E8)

// ---------------------------------------------------------------------------
// Data model
// ---------------------------------------------------------------------------

data class FlashCard(
    val question: String,
    val answer: String
)

// ---------------------------------------------------------------------------
// Screen
// ---------------------------------------------------------------------------

@Composable
fun FlashCardScreen(
    cards: List<FlashCard>,
    error: String?,
    onRetry: () -> Unit = {}
) {
    var currentIndex  by remember { mutableIntStateOf(0) }
    var knowCount     by remember { mutableIntStateOf(0) }
    var dontKnowCount by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {

        // ── "X of Y cards reviewed" ───────────────────────────────────────
        if (cards.isNotEmpty()) {
            Text(
                text     = "$currentIndex of ${cards.size} cards reviewed",
                style    = MaterialTheme.typography.bodyMedium,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
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

                // ── Card stack  (fills remaining vertical space) ──────────
                Box(
                    modifier         = Modifier
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
                                    scaleX       = 1f - (i * 0.05f)
                                    scaleY       = 1f - (i * 0.05f)
                                    translationY = i * 20f
                                    alpha        = 1f - (i * 0.2f)
                                }
                            ) {
                                if (isTopCard) {
                                    SwipeCard(
                                        // Swipe LEFT  = Don't Know
                                        onSwipeLeft  = {
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
                                            card          = cards[cardIndex],
                                            cardIndex     = cardIndex,
                                            isInteractive = true
                                        )
                                    }
                                } else {
                                    FlashCardContent(
                                        card          = cards[cardIndex],
                                        cardIndex     = cardIndex,
                                        isInteractive = false
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // ── Count tiles row ───────────────────────────────────────
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ReviewCountTile(
                        count           = dontKnowCount,
                        label           = "DON'T KNOW",
                        backgroundColor = ColorDontKnowBackground,
                        countColor      = ColorDontKnowAccent,
                        labelColor      = ColorDontKnowAccent,
                        modifier        = Modifier.weight(1f)
                    )
                    ReviewCountTile(
                        count           = knowCount,
                        label           = "KNOW",
                        backgroundColor = ColorKnowBackground,
                        countColor      = ColorKnowAccent,
                        labelColor      = ColorKnowAccent,
                        modifier        = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(12.dp))

                // ── Action buttons row ────────────────────────────────────
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick   = {
                            dontKnowCount++
                            if (currentIndex < cards.size) currentIndex++
                        },
                        modifier  = Modifier.weight(1f).height(60.dp),
                        shape     = RoundedCornerShape(50.dp),
                        colors    = ButtonDefaults.buttonColors(
                            containerColor = ColorDontKnowButton,
                            contentColor   = Color(0xFF333333)
                        ),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Text(
                            text          = "DON'T KNOW",
                            fontSize      = 14.sp,
                            fontWeight    = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Button(
                        onClick   = {
                            knowCount++
                            if (currentIndex < cards.size) currentIndex++
                        },
                        modifier  = Modifier.weight(1f).height(60.dp),
                        shape     = RoundedCornerShape(50.dp),
                        colors    = ButtonDefaults.buttonColors(
                            containerColor = ColorKnowButton,
                            contentColor   = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Text(
                            text          = "KNOW",
                            fontSize      = 14.sp,
                            fontWeight    = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Count tile
// ---------------------------------------------------------------------------

@Composable
fun ReviewCountTile(
    count:           Int,
    label:           String,
    backgroundColor: Color,
    countColor:      Color,
    labelColor:      Color,
    modifier:        Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(color = backgroundColor, shape = RoundedCornerShape(16.dp))
            .padding(vertical = 16.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text       = count.toString(),
            fontSize   = 28.sp,
            fontWeight = FontWeight.Bold,
            color      = countColor
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text          = label,
            fontSize      = 11.sp,
            fontWeight    = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            color         = labelColor
        )
    }
}

// ---------------------------------------------------------------------------
// Swipe wrapper
// ---------------------------------------------------------------------------

@Composable
fun SwipeCard(
    onSwipeLeft:       () -> Unit = {},
    onSwipeRight:      () -> Unit = {},
    swipeThreshold:    Float = 300f,
    sensitivityFactor: Float = 3f,
    content: @Composable () -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    val density = LocalDensity.current.density
    val scope   = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.value.roundToInt(), 0) }
            .graphicsLayer {
                rotationZ = (offsetX.value / 30f).coerceIn(-15f, 15f)
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        when {
                            offsetX.value > swipeThreshold -> {
                                scope.launch {
                                    offsetX.animateTo(2000f, spring(stiffness = 200f))
                                    onSwipeRight()
                                    offsetX.snapTo(0f)
                                }
                            }
                            offsetX.value < -swipeThreshold -> {
                                scope.launch {
                                    offsetX.animateTo(-2000f, spring(stiffness = 200f))
                                    onSwipeLeft()
                                    offsetX.snapTo(0f)
                                }
                            }
                            else -> {
                                scope.launch {
                                    offsetX.animateTo(
                                        0f,
                                        spring(dampingRatio = 0.6f, stiffness = 300f)
                                    )
                                }
                            }
                        }
                    }
                ) { change, dragAmount ->
                    if (change.positionChange() != Offset.Zero) change.consume()
                    scope.launch {
                        offsetX.snapTo(offsetX.value + (dragAmount / density) * sensitivityFactor)
                    }
                }
            }
    ) { content() }
}

// ---------------------------------------------------------------------------
// Card face content
// ---------------------------------------------------------------------------

@Composable
fun FlashCardContent(
    card:          FlashCard,
    cardIndex:     Int,
    isInteractive: Boolean = true
) {
    var face by remember(cardIndex) { mutableStateOf(CardFace.Front) }

    FlipCard(
        face     = face,
        onClick  = {
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
                colors   = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Box(
                    modifier         = Modifier.fillMaxSize().padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text  = "Question ${cardIndex + 1}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text  = card.question,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        if (isInteractive) {
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text  = "Tap to flip • Swipe to continue",
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
                colors   = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Box(
                    modifier         = Modifier.fillMaxSize().padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text  = "Answer ${cardIndex + 1}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text  = card.answer,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        if (isInteractive) {
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text  = "Tap to flip • Swipe to continue",
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

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

private val sampleCards = listOf(
    FlashCard("What is the capital of France?", "Paris"),
    FlashCard("What is 2 + 2?",                 "4"),
    FlashCard("Who wrote Romeo and Juliet?",     "William Shakespeare"),
    FlashCard("What is the speed of light?",     "299,792,458 m/s"),
    FlashCard("What is H2O?",                    "Water"),
)

@Preview(name = "Full Screen", showBackground = true)
@Composable
private fun PreviewFlashCardScreenFull() {
    BrainestTheme {
        Surface(color = Color(0xFFF7F7F7)) {
            FlashCardScreen(cards = sampleCards, error = null)
        }
    }
}

@Preview(name = "Empty state", showBackground = true)
@Composable
private fun PreviewFlashCardEmpty() {
    BrainestTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            FlashCardScreen(cards = emptyList(), error = null)
        }
    }
}

@Preview(name = "Error state", showBackground = true)
@Composable
private fun PreviewFlashCardError() {
    BrainestTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            FlashCardScreen(cards = emptyList(), error = "Failed to load cards.")
        }
    }
}