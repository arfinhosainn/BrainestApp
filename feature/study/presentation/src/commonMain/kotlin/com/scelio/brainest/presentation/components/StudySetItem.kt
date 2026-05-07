package com.scelio.brainest.presentation.components

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brainest.feature.study.presentation.generated.resources.Res
import brainest.feature.study.presentation.generated.resources.audiobackground
import brainest.feature.study.presentation.generated.resources.idlebackground
import brainest.feature.study.presentation.generated.resources.pdfwordbackground
import com.scelio.brainest.designsystem.BrainestTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

private val ColorCardBackground = Color.White
private val ColorDateBadge = Color(0xFF1898E7)
private val ColorTitle = Color(0xFF2B3A35)
private val ColorSubtitle = Color(0xFF6D7572)
private val ColorExp = Color(0xFF19B465)

enum class DocType { DOCUMENT, AUDIO, OTHER }

@Composable
fun StudySetItem(
    id: String,
    title: String,
    createdAt: String,
    flashcardsCount: Int,
    quizCount: Int,
    docType: DocType = DocType.DOCUMENT,
    onSetClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val illustration = when (docType) {
        DocType.DOCUMENT -> Res.drawable.pdfwordbackground
        DocType.AUDIO -> Res.drawable.audiobackground
        DocType.OTHER -> Res.drawable.idlebackground
    }
    val sourceDescription = when (docType) {
        DocType.DOCUMENT -> "PDF, Word, text docs"
        DocType.AUDIO -> "Audio upload / recording"
        DocType.OTHER -> "PPT, images, or idle/default"
    }
    val estimatedExp = (flashcardsCount + quizCount).coerceAtLeast(1) * 2

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { onSetClick(id) }),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = ColorCardBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, end = 14.dp, top = 14.dp, bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(102.dp)
                        .height(148.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(illustration),
                        contentDescription = sourceDescription,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ColorTitle,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = sourceDescription,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = ColorSubtitle,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "\uD83C\uDF81", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "~$estimatedExp EXP",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ColorExp
                        )
                    }
                }
            }
        }

        DateBadge(
            text = createdAt,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(y = (-12).dp)
                .padding(end = 14.dp)
        )
    }
}

@Composable
private fun DateBadge(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = ColorDateBadge,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 0.dp
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
        )
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
                    onSetClick = {}
                )
            }
        }
    }
}
