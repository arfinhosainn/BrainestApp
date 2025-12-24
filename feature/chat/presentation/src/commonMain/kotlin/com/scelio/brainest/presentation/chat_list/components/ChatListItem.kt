package com.scelio.brainest.presentation.chat_list.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import brainest.feature.chat.presentation.generated.resources.Res
import brainest.feature.chat.presentation.generated.resources.arrow_right
import brainest.feature.chat.presentation.generated.resources.ic_delete
import brainest.feature.chat.presentation.generated.resources.improve_style
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.extended
import com.scelio.brainest.presentation.model.ChatItemUi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.roundToInt

@Composable
fun ChatListItem(
    chat: ChatItemUi,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    leadingIcon: Painter? = null,
    iconSize: Dp = 24.dp,
    iconTint: Color = Color.Unspecified,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    fontFamily: FontFamily? = MaterialTheme.typography.bodyMedium.fontFamily,
    fontWeight: FontWeight = FontWeight.Medium,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.extended.secondaryFill,
    cornerRadius: Dp = 20.dp,
    elevation: Dp = 0.2.dp
) {
    var isRemoved by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    var offsetX by remember { mutableFloatStateOf(0f) }
    val deleteButtonWidth = with(LocalDensity.current) { 80.dp.toPx() }
    val swipeThreshold = -deleteButtonWidth * 0.2f

    val draggableState = rememberDraggableState { delta ->
        val newOffset = offsetX + delta
        offsetX = newOffset.coerceIn(-deleteButtonWidth, 0f)
    }

    AnimatedVisibility(
        visible = !isRemoved,
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = 300),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        Box(
            modifier = modifier.fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.CenterEnd,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(cornerRadius)
                    )
                    .padding(horizontal = 20.dp)
            ) {
                IconButton(
                    onClick = {
                        isRemoved = true
                        coroutineScope.launch {
                            delay(300)
                            onDelete()
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_delete),
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(offsetX.roundToInt(), 0) }
                    .draggable(
                        state = draggableState,
                        orientation = Orientation.Horizontal,
                        onDragStopped = {
                            if (offsetX < swipeThreshold) {
                                offsetX = -deleteButtonWidth
                            } else {
                                offsetX = 0f
                            }
                        }
                    ),
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                shape = RoundedCornerShape(cornerRadius),
                elevation = CardDefaults.cardElevation(defaultElevation = elevation)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(cornerRadius))
                        .background(backgroundColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true)
                        ) { onClick() }
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        leadingIcon?.let { icon ->
                            Icon(
                                painter = icon,
                                modifier = Modifier.size(iconSize),
                                contentDescription = null,
                                tint = iconTint
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                        }

                        Text(
                            text = chat.title,
                            style = textStyle,
                            color = textColor,
                            fontFamily = fontFamily,
                            fontWeight = fontWeight,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Icon(
                        painter = painterResource(Res.drawable.arrow_right),
                        contentDescription = "Navigate",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}


@Preview
@Composable
private fun ChatListItemUiPreview() {
    BrainestTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            ChatListItem(
                chat = ChatItemUi(
                    id = "1",
                    title = "How to improve my UI design skills?",
                    lastMessage = "You should practice daily...",
                    timestamp = kotlin.time.Clock.System.now(),
                    model = "GPT-4",
                    unreadCount = 2,
                    isSelected = false
                ),
                leadingIcon = painterResource(Res.drawable.improve_style),
                onClick = { /* Handle click */ },
                onDelete = { /* Handle delete */ }
            )
        }
    }
}


@Preview
@Composable
private fun ChatListItemDarkUiPreview() {
    BrainestTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            ChatListItem(
                chat = ChatItemUi(
                    id = "1",
                    title = "How to improve my UI design skills?",
                    lastMessage = "You should practice daily...",
                    timestamp = kotlin.time.Clock.System.now(),
                    model = "GPT-4",
                    unreadCount = 2,
                    isSelected = false
                ),
                leadingIcon = painterResource(Res.drawable.improve_style),
                onClick = { /* Handle click */ },
                onDelete = { /* Handle delete */ }
            )
        }
    }
}



