package com.scelio.brainest.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.MicNone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.scelio.brainest.designsystem.BrainestTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

private val ColorAccentBar = Color(0xFF006D3C)
private val ColorTextDark = Color(0xFF1A1A1A)
private val ColorTextMuted = Color(0xFF6D6A66)

private val ColorIconBg = Color(0xFFE2EAE0)
private val ColorAudioIconBg = Color(0xFFFFB786)
private val ColorAudioAccent = Color(0xFF964900)

enum class DocType { DOCUMENT, AUDIO, OTHER }

@Composable
fun StudySetItem(
    id: String,
    title: String,
    createdAt: String,
    flashcardsCount: Int,
    quizCount: Int,
    flashcardsSwiped: Int = 0,
    quizzesCompleted: Int = 0,
    masteryPercent: Int = 0,
    docType: DocType = DocType.DOCUMENT,
    onSetClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = { onSetClick(id) }),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Left icon - change icon and background depending on document type
                val (iconVector, iconBg, iconTint) = when (docType) {
                    DocType.AUDIO -> Triple(
                        Icons.Outlined.MicNone,
                        ColorAudioIconBg,
                        ColorAudioAccent
                    )

                    DocType.DOCUMENT -> Triple(
                        Icons.Outlined.Description,
                        ColorIconBg,
                        ColorAccentBar
                    )

                    else -> Triple(Icons.Outlined.Description, ColorIconBg, ColorAccentBar)
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ColorTextDark,
                        lineHeight = 28.sp,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "$flashcardsCount flashcards · $createdAt",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = ColorTextMuted
                    )
                }

                Text(
                    text = "⋮",
                    fontSize = 20.sp,
                    color = ColorTextMuted,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .offset(y = (-6).dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mastery row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mastery",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = ColorTextMuted
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "${masteryPercent.coerceIn(0, 100)}%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ColorAccentBar
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFEBEFEA))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = (masteryPercent.coerceIn(0, 100) / 100f))
                        .height(12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ColorAccentBar)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewStudySetItem() {
    BrainestTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.padding(16.dp)) {
                StudySetItem(
                    id = "preview-id",
                    title = "Mitochondria",
                    createdAt = "Apr 5, 2026",
                    flashcardsCount = 20,
                    quizCount = 10,
                    flashcardsSwiped = 42,
                    quizzesCompleted = 3,
                    masteryPercent = 85,
                    onSetClick = {}
                )
            }
        }
    }
}
