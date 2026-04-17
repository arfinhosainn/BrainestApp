package com.scelio.brainest.presentation.chat_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import brainest.feature.chat.presentation.generated.resources.Res
import brainest.feature.chat.presentation.generated.resources.brainest
import brainest.feature.chat.presentation.generated.resources.chats
import brainest.feature.chat.presentation.generated.resources.empty_chat
import brainest.feature.chat.presentation.generated.resources.new_chat
import brainest.feature.chat.presentation.generated.resources.no_recent_chats
import brainest.feature.chat.presentation.generated.resources.recents
import com.scelio.brainest.designsystem.BricolageGrotesq
import com.scelio.brainest.presentation.model.ChatItemUi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ChatHistoryDrawer(
    recentChats: List<ChatItemUi>,
    currentChatId: String?,
    onNewChatClick: () -> Unit,
    onChatsClick: () -> Unit,
    onChatSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val drawerItemHorizontalPadding = 12.dp
    val drawerLeadingSlotWidth = 20.dp
    val drawerLeadingSpacing = 12.dp

    LazyColumn(
        modifier = modifier
            .fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = stringResource(Res.string.brainest),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                fontFamily = BricolageGrotesq
            )
        }

        item {
            Spacer(Modifier.height(4.dp))
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(onClick = onNewChatClick)
                    .padding(horizontal = drawerItemHorizontalPadding, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.width(drawerLeadingSlotWidth),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = null,
                        tint = Color(0xFFE46D43),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(drawerLeadingSpacing))
                Text(
                    text = stringResource(Res.string.new_chat),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                onClick = onChatsClick
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = drawerItemHorizontalPadding, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.width(drawerLeadingSlotWidth),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.empty_chat),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(Modifier.width(drawerLeadingSpacing))
                    Text(
                        text = stringResource(Res.string.chats),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        item {
            Spacer(Modifier.height(4.dp))
        }

        item {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
        }

        item {
            Spacer(Modifier.height(2.dp))
        }

        item {
            Text(
                text = stringResource(Res.string.recents),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = drawerItemHorizontalPadding)
            )
        }

        if (recentChats.isEmpty()) {
            item {
                Text(
                    text = stringResource(Res.string.no_recent_chats),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = drawerItemHorizontalPadding, top = 4.dp)
                )
            }
        } else {
            items(
                items = recentChats,
                key = { it.id }
            ) { chat ->
                val isSelected = chat.id == currentChatId
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isSelected) {
                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f)
                            } else {
                                Color.Transparent
                            }
                        )
                        .clickable { onChatSelected(chat.id) }
                        .padding(horizontal = drawerItemHorizontalPadding, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(
                        text = chat.title,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
