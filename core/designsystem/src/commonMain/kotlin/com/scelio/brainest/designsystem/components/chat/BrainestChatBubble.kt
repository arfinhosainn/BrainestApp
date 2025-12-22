package com.scelio.brainest.designsystem.components.chat


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.extended
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalFoundationApi::class) // Required for combinedClickable in KMP
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

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val maxBubbleWidth = maxWidth * 0.75f // 75% of the available parent width

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isFromUser) Arrangement.End else Arrangement.Start
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = maxBubbleWidth) // Limit bubble width based on constraints
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
                Text(
                    text = messageContent,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.extended.textPrimary
                )
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
            messageContent = "Hello world, this is a longer message that hopefully spans over multiple lines so we can see how the preview would look like for that as well.",
            isFromUser = true,
            formattedDateTime = "Friday 2:20pm",
        )
    }
}

@Composable
@Preview
fun BrainestChatBubbleRightPreview() {
    BrainestTheme(darkTheme = false) {
        BrainestChatBubble(
            messageContent = "Hello world, this is a longer message that hopefully spans over multiple lines so we can see how the preview would look like for that as well.",
            isFromUser = false,
            formattedDateTime = "Friday 2:20pm",
        )
    }
}