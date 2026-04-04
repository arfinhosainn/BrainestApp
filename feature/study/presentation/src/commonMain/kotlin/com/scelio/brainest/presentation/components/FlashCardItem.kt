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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brainest.feature.study.presentation.generated.resources.Res
import brainest.feature.study.presentation.generated.resources.ic_flashcard
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.BricolageGrotesq
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

private val ColorAccentBar = Color(0xFF19C472)
private val ColorBrand = Color(0xFF19C472)
private val ColorTextDark = Color(0xFF1A1A1A)
private val ColorTextMuted = Color(0xFF6D6A66)
private val ColorCardBg = Color(0xFFFBF8F5)
private val ColorDivider = Color(0xFFEDE3D8)
private val ColorIconBg = Color(0xFFD8F5E2)

@Composable
fun FlashcardItem(
    title: String,
    generatedAt: String,
    totalCards: Int,
    totalSwiped: Int,
    onOpenClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ColorCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
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
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = ColorTextDark,
                        lineHeight = 28.sp
                    )

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ColorIconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.ic_flashcard),
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(ColorDivider)
                )

                Spacer(Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatBlock(label = "Generated", value = generatedAt)
                    StatBlock(label = "Cards", value = totalCards.toString())
                    StatBlock(label = "Swiped", value = totalSwiped.toString())
                }

                Spacer(Modifier.height(18.dp))

                Button(
                    onClick = onOpenClick,
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ColorBrand),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = "REVIEW",
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

@Composable
private fun StatBlock(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = ColorTextMuted
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = ColorTextDark
        )
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
                    title = "Cellular Mitosis",
                    generatedAt = "Apr 4, 2026",
                    totalCards = 20,
                    totalSwiped = 18,
                    onOpenClick = {}
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
                    title = "Cellular Mitosis",
                    generatedAt = "Apr 4, 2026",
                    totalCards = 20,
                    totalSwiped = 18,
                    onOpenClick = {}
                )
                FlashcardItem(
                    title = "Newton's Laws",
                    generatedAt = "Apr 2, 2026",
                    totalCards = 30,
                    totalSwiped = 5,
                    onOpenClick = {}
                )
            }
        }
    }
}
