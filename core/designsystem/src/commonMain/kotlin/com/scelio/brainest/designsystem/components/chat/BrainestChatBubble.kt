package com.scelio.brainest.designsystem.components.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.components.chat.chat_math.ContentSegment
import com.scelio.brainest.designsystem.components.chat.chat_math.InlineMath
import com.scelio.brainest.designsystem.components.chat.chat_math.ListType
import com.scelio.brainest.designsystem.components.chat.chat_math.intToRoman
import com.scelio.brainest.designsystem.components.chat.chat_math.parseContentSegments
import com.scelio.brainest.designsystem.extended
import io.github.darriousliu.katex.core.MTMathView
import io.github.darriousliu.katex.core.MTMathViewMode
import io.github.darriousliu.katex.core.MTTextAlignment
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BrainestChatBubble(
    messageContent: String,
    isFromUser: Boolean,
    formattedDateTime: String,
    modifier: Modifier = Modifier,
    color: Color? = null,
    messageStatus: @Composable (() -> Unit)? = null,
    triangleSize: Dp = 16.dp,
    onLongClick: (() -> Unit)? = null
) {
    val padding = 12.dp
    val trianglePosition = if (isFromUser) TrianglePosition.RIGHT else TrianglePosition.LEFT
    val bubbleColor = color ?: if (isFromUser) {
        MaterialTheme.colorScheme.extended.chatUserBubble
    } else {
        Color.Transparent
    }

    val textColor = MaterialTheme.colorScheme.extended.textPrimary

    val segments = remember(messageContent) { parseContentSegments(messageContent) }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {

        val maxBubbleWidth = if (isFromUser) maxWidth * 0.6f else maxWidth

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isFromUser) Arrangement.End else Arrangement.Start
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = maxBubbleWidth)
                    .then(
                        if (onLongClick != null) {
                            Modifier.combinedClickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(
                                    color = MaterialTheme.colorScheme.extended.surfaceOutline
                                ),
                                onLongClick = onLongClick,
                                onClick = {}
                            )
                        } else Modifier
                    )
                    .clip(
                        ChatBubbleShape(
                            trianglePosition = trianglePosition,
                        )
                    )
                    .background(bubbleColor)
                    .padding(
                        start = if (trianglePosition == TrianglePosition.LEFT) {
                            padding + triangleSize
                        } else padding,
                        end = if (trianglePosition == TrianglePosition.RIGHT) {
                            padding + triangleSize
                        } else padding,
                        top = padding,
                        bottom = padding
                    ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    segments.forEach { segment ->
                        when (segment) {
                            is ContentSegment.HeadingSegment -> {
                                val fontSize = when (segment.level) {
                                    1 -> 20.sp
                                    2 -> 18.sp
                                    else -> 16.sp
                                }
                                InlineMath(
                                    text = segment.text,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = fontSize,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = textColor,
                                    mathFontSize = fontSize
                                )
                            }

                            is ContentSegment.TextWithInlineMathSegment -> {
                                val normalizedText = segment.originalText.trim()
                                val isSectionLabel = !isFromUser &&
                                    (normalizedText == "Answer" || normalizedText == "Explanation")
                                val isAnswerLine = !isFromUser &&
                                    normalizedText.startsWith("The answer is")

                                InlineMath(
                                    text = segment.originalText,
                                    style = when {
                                        isSectionLabel -> MaterialTheme.typography.titleLarge.copy(
                                            fontSize = if (normalizedText == "Answer") 24.sp else 21.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        isAnswerLine -> MaterialTheme.typography.titleMedium.copy(
                                            fontSize = 22.sp,
                                            lineHeight = 30.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        else -> MaterialTheme.typography.bodyLarge
                                    },
                                    color = textColor,
                                    mathFontSize = when {
                                        isSectionLabel -> 22.sp
                                        isAnswerLine -> 22.sp
                                        else -> if (!isFromUser) 20.sp else 16.sp
                                    }
                                )
                            }

                            is ContentSegment.DisplayMathSegment -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(15.dp)
                                        .horizontalScroll(rememberScrollState()),
                                    contentAlignment = Alignment.Center
                                ) {
                                    MTMathView(
                                        latex = segment.latex,
                                        fontSize = if (isFromUser) 18.sp else 22.sp,
                                        textColor = textColor,
                                        mode = MTMathViewMode.KMTMathViewModeDisplay,
                                        textAlignment = MTTextAlignment.KMTTextAlignmentCenter,
                                        displayErrorInline = true
                                    )
                                }
                            }

                            is ContentSegment.ListItemSegment -> {
                                Row(
                                    modifier = Modifier.padding(start = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    val marker = when (segment.type) {
                                        ListType.BULLET -> "•"
                                        ListType.NUMBERED -> null
                                        ListType.ROMAN -> "${intToRoman(segment.number ?: 1)}."
                                    }

                                    if (segment.type == ListType.NUMBERED && !isFromUser) {
                                        Box(
                                            modifier = Modifier
                                                .padding(top = 2.dp, end = 10.dp)
                                                .size(30.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    MaterialTheme.colorScheme.surfaceVariant
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = (segment.number ?: 1).toString(),
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    } else if (marker != null) {
                                        Text(
                                            text = marker,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = textColor,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                    }

                                    InlineMath(
                                        text = segment.text,
                                        style = if (segment.type == ListType.NUMBERED && !isFromUser) {
                                            MaterialTheme.typography.titleSmall.copy(
                                                fontSize = 19.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        } else {
                                            MaterialTheme.typography.bodyLarge
                                        },
                                        color = textColor,
                                        mathFontSize = if (segment.type == ListType.NUMBERED && !isFromUser) {
                                            22.sp
                                        } else {
                                            if (!isFromUser) 20.sp else 16.sp
                                        }
                                    )
                                }
                            }

                            is ContentSegment.CodeBlockSegment -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(
                                                alpha = 0.5f
                                            ),
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = segment.code,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 14.sp,
                                        color = textColor
                                    )
                                }
                            }

                            is ContentSegment.TableSegment -> {
                                Text(
                                    text = "[Table content]",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontStyle = FontStyle.Italic,
                                    color = textColor.copy(alpha = 0.7f)
                                )
                            }

                            is ContentSegment.SpacerSegment -> {
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = if (isFromUser) Arrangement.End else Arrangement.Start
                ) {
                    Text(
                        text = formattedDateTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.extended.textSecondary,
                    )
                    if (messageStatus != null) {
                        Spacer(modifier = Modifier.width(4.dp))
                        messageStatus()
                    }
                }
            }
        }
    }
}


@Composable
@Preview
fun BrainestChatMathFlowPreview() {
    BrainestTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isSystemInDarkTheme()) Color.Black else Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val x = "x"

            // 1. USER MESSAGE
            BrainestChatBubble(
                messageContent = "Solve the math $3x - 7 = n$ for $x$.",
                isFromUser = true,
                formattedDateTime = "2:45pm",
            )

            // 2. AI RESPONSE
            BrainestChatBubble(
                messageContent = """
                    To solve for '$x$' in the equation $3x - 7 = n$, follow these steps:
                    
                    1. Add 7 to both sides of the equation:
                    $$ 3x = n + 7 $$
                    
                    2. Divide both sides by 3 to isolate $x$:
                    $$ x = \frac{n + 7}{3} $$
                    
                    Therefore, the solution for $x$ is expressed as:
                    $$ x = \frac{1}{3}n + \frac{7}{3} $$
                """.trimIndent(),
                isFromUser = false,
                formattedDateTime = "2:45pm",
            )
        }
    }
}
