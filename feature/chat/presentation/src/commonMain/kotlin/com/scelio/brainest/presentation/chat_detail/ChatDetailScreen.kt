package com.scelio.brainest.presentation.chat_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scelio.brainest.designsystem.components.chat.BrainestChatBubble
import com.scelio.brainest.presentation.chat_detail.components.ChatDetailHeader
import com.scelio.brainest.presentation.chat_detail.components.MessageInputBox
import com.scelio.brainest.presentation.chat_list.components.ChatListHeader
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatDetailScreen(
    chatId: String?,
    userId: String,
    viewModel: ChatDetailViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userId, chatId) {
        viewModel.setUserId(userId)
        viewModel.onAction(ChatDetailAction.OnSelectChat(chatId))
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is ChatDetailEvent.OnError -> {
                    snackbarHostState.showSnackbar(event.error.toString())
                }

                ChatDetailEvent.OnNewMessage -> {
                }
            }
        }
    }

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem?.index == layoutInfo.totalItemsCount - 1
        }.collectLatest { atBottom ->
            if (atBottom && state.messages.isNotEmpty()) {
                viewModel.onAction(ChatDetailAction.OnScrollToTop)
            }
        }
    }

    Scaffold(
        topBar = {
            ChatDetailHeader(
                scrollState = listState,
                onCloseClick = { /* handle */ },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                reverseLayout = true
            ) {
                if (state.isLoading) {
                    item {
                        Box(
                            Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                }

                items(
                    items = state.messages,
                    key = { it.id }
                ) { msg ->
                    BrainestChatBubble(
                        messageContent = msg.content,
                        isFromUser = msg.isFromUser,
                        formattedDateTime = msg.timestamp.asString()
                    )
                }

                if (state.isPaginationLoading) {
                    item {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            MessageInputBox(
                textFieldState = viewModel.messageTextFieldState,
                selectedImages = emptyList(),
                selectedDocument = null,
                enabled = !state.isLoading,
                onSendMessage = {
                    if (state.canSendMessage) {
                        viewModel.onAction(ChatDetailAction.OnSendMessageClick)
                    }
                },
                onImageSelected = { /* TODO: Handle image selection */ },
                onImageRemoved = { /* TODO: Handle image removal */ },
                onDocumentSelected = { /* TODO: Handle document selection */ },
                onDocumentCleared = { /* TODO: Handle document clear */ },
                onGalleryClick = { /* TODO: Handle gallery click */ },
                onCameraClick = { /* TODO: Handle camera click */ },
                onDocumentClick = { /* TODO: Handle document click */ },
                onMicClick = { /* TODO: Handle mic click */ }
            )
        }
    }
}