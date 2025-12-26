package com.scelio.brainest.designsystem.components.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.scelio.brainest.designsystem.components.chat.chat_math.InlineMathText
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

    // Define text colors
    val textColor = MaterialTheme.colorScheme.extended.textPrimary

    // Parse content into segments (memoized)
    val segments = remember(messageContent) { parseContentSegments(messageContent) }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val maxBubbleWidth = maxWidth * 0.75f // 75% of the available parent width

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
                            triangleSize = triangleSize
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
                // --- RENDER CONTENT SEGMENTS ---
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
                                InlineMathText(
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
                                InlineMathText(
                                    text = segment.originalText,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = textColor,
                                    mathFontSize = 16.sp
                                )
                            }

                            is ContentSegment.DisplayMathSegment -> {
                                // Scrollable container for block math
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(8.dp)
                                        .horizontalScroll(rememberScrollState()),
                                    contentAlignment = Alignment.Center
                                ) {
                                    MTMathView(
                                        latex = segment.latex,
                                        fontSize = 18.sp,
                                        textColor = textColor,
                                        mode = MTMathViewMode.KMTMathViewModeDisplay,
                                        textAlignment = MTTextAlignment.KMTTextAlignmentCenter,
                                        displayErrorInline = true
                                    )
                                }
                            }

                            is ContentSegment.ListItemSegment -> {
                                Row(
                                    modifier = Modifier.padding(start = 8.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    val marker = when (segment.type) {
                                        ListType.BULLET -> "•"
                                        ListType.NUMBERED -> "${segment.number}."
                                        ListType.ROMAN -> "${intToRoman(segment.number ?: 1)}."
                                    }
                                    Text(
                                        text = marker,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                        color = textColor,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    InlineMathText(
                                        text = segment.text,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = textColor,
                                        mathFontSize = 16.sp
                                    )
                                }
                            }

                            is ContentSegment.CodeBlockSegment -> {
                                // Simple render for code blocks as plain text for now
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
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
                                // Placeholder for tables (omitted as requested)
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

                // --- TIMESTAMP AND STATUS ---
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
fun BrainestChatBubbleLeftPreview() {
    BrainestTheme(darkTheme = true) {
        BrainestChatBubble(
            messageContent = "Here is an equation: $$ x = \\frac{-b \\pm \\sqrt{b^2 - 4ac}}{2a} $$",
            isFromUser = false,
            formattedDateTime = "Friday 2:20pm",
        )
    }
}

@Composable
@Preview
fun BrainestChatBubbleRightPreview() {
    BrainestTheme(darkTheme = false) {
        BrainestChatBubble(
            messageContent = "Solve for $ x $ in the equation above.",
            isFromUser = false,
            formattedDateTime = "Friday 2:20pm",
        )
    }
}