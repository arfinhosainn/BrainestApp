package com.scelio.brainest.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brainest.feature.flashcards.presentation.generated.resources.Res
import brainest.feature.flashcards.presentation.generated.resources.ic_flashcard
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.BricolageGrotesq
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

private val ColorAccentBar = Color(0xFF33CA67)
private val ColorBrandBrown = Color(0xFF33CA67)
private val ColorTextDark = Color(0xFF1A1A1A)
private val ColorTextMuted = Color(0xFFD8F5E2)
private val ColorBeigeBg = Color(0xFFF2EBE1)
private val ColorIconBg = Color(0xFFD8F5E2)
private val ColorCardBg = Color(0xFFFBF8F5)
private val ColorDivider = Color(0xFFEDE3D8)

@Composable
fun FlashcardItem(
    category: String,
    title: String,
    progress: Int,
    totalCards: Int,
    lastPlayed: String,
    onResumeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progressFraction = remember(progress, totalCards) {
        if (totalCards > 0) progress.toFloat() / totalCards else 0f
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ColorCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
        ) {

            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(ColorAccentBar)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = category.uppercase(),
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.5.sp
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = ColorTextDark,
                            lineHeight = 30.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(ColorIconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.ic_flashcard),
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier
                                .size(26.dp)
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(ColorDivider)
                )

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PROGRESS",
                        color = ColorTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "$progress / $totalCards",
                        color = ColorBrandBrown,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = ColorBrandBrown,
                    trackColor = ColorBeigeBg,
                    strokeCap = StrokeCap.Round,
                )

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Last played",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                        Text(
                            text = lastPlayed,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ColorTextDark
                        )
                    }

                    Button(
                        onClick = onResumeClick,
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ColorBrandBrown),
                        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 11.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text(
                            text = "RESUME",
                            fontSize = 13.sp,
                            fontFamily = BricolageGrotesq,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "FlashcardItem — single", showBackground = true)
@Composable
private fun PreviewFlashcardItem() {
    BrainestTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFF0EBE3)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                FlashcardItem(
                    category = "Biology",
                    title = "Cellular Mitosis",
                    progress = 18,
                    totalCards = 20,
                    lastPlayed = "2h ago",
                    onResumeClick = {}
                )
            }
        }
    }
}

@Preview(name = "FlashcardItem — list")
@Composable
private fun PreviewFlashcardItemList() {
    BrainestTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFF0EBE3)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FlashcardItem(
                    category = "Biology",
                    title = "Cellular Mitosis",
                    progress = 18,
                    totalCards = 20,
                    lastPlayed = "2h ago",
                    onResumeClick = {}
                )
                FlashcardItem(
                    category = "Physics",
                    title = "Newton's Laws",
                    progress = 5,
                    totalCards = 30,
                    lastPlayed = "Yesterday",
                    onResumeClick = {}
                )
            }
        }
    }
}