package com.scelio.brainest.presentation.chat_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import brainest.feature.chat.presentation.generated.resources.ic_search
import com.scelio.brainest.presentation.model.ChatItemUi
import org.jetbrains.compose.resources.painterResource

@Composable
fun ChatHistoryDrawer(
    recentChats: List<ChatItemUi>,
    currentChatId: String?,
    onNewChatClick: () -> Unit,
    onChatsClick: () -> Unit,
    onChatSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 18.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Brainest",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onNewChatClick)
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = null,
                tint = Color(0xFFE46D43),
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = "New chat",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFE46D43),
                fontWeight = FontWeight.Medium
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
            onClick = onChatsClick
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_search),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Chats",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(Modifier.height(6.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
        Spacer(Modifier.height(2.dp))

        Text(
            text = "Recents",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (recentChats.isEmpty()) {
            Text(
                text = "No recent chats yet",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
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
                            .padding(horizontal = 10.dp, vertical = 10.dp),
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
}
