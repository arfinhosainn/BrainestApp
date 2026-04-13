package com.scelio.brainest.presentation.chat_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scelio.brainest.designsystem.components.chat.BrainestChatBubble
import com.scelio.brainest.presentation.chat_detail.components.ChatConversationShimmer
import com.scelio.brainest.presentation.chat_detail.components.ChatPaginationShimmer
import com.scelio.brainest.presentation.chat_detail.components.ChatDetailHeader
import com.scelio.brainest.presentation.chat_detail.components.ChatHistoryDrawer
import com.scelio.brainest.presentation.chat_detail.components.MessageInputBox
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Suppress("ParamsComparedByRef")
@Composable
fun ChatDetailScreen(
    chatId: String?,
    userId: String,
    onNewChatClick: () -> Unit,
    onOpenChatsClick: () -> Unit,
    onNavigateToChat: (String) -> Unit,
    onCloseClick: () -> Unit,
    viewModel: ChatDetailViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val latestState by rememberUpdatedState(state)
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val messageTextFieldState = rememberTextFieldState()

    LaunchedEffect(state.messageText) {
        if (messageTextFieldState.text.toString() != state.messageText) {
            messageTextFieldState.edit {
                replace(0, length, state.messageText)
            }
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { messageTextFieldState.text.toString() }
            .collect { text ->
                viewModel.onAction(ChatDetailAction.OnMessageTextChanged(text))
            }
    }

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

    LaunchedEffect(chatId, viewModel.messages.size) {
        if (viewModel.messages.isNotEmpty() && (latestState.isNearBottom || viewModel.messages.size <= 1)) {
            listState.scrollToItem(0)
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow {
            listState.firstVisibleItemIndex <= 1 && listState.firstVisibleItemScrollOffset < 40
        }.distinctUntilChanged().collectLatest { isNearBottom ->
            viewModel.updateScrollPosition(isNearBottom)
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem?.index == layoutInfo.totalItemsCount - 1
        }.distinctUntilChanged().collectLatest { atTopOfHistory ->
            if (atTopOfHistory && viewModel.messages.isNotEmpty()) {
                viewModel.onAction(ChatDetailAction.OnScrollToTop)
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                ChatHistoryDrawer(
                    recentChats = state.recentChats,
                    currentChatId = state.chatUi?.id,
                    onNewChatClick = {
                        coroutineScope.launch { drawerState.close() }
                        onNewChatClick()
                    },
                    onChatsClick = {
                        onOpenChatsClick()
                    },
                    onChatSelected = { selectedChatId ->
                        coroutineScope.launch { drawerState.close() }
                        if (selectedChatId != state.chatUi?.id) {
                            onNavigateToChat(selectedChatId)
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                ChatDetailHeader(
                    scrollState = listState,
                    onMenuClick = {
                        coroutineScope.launch { drawerState.open() }
                    },
                    onCloseClick = onCloseClick,
                )
            },
            bottomBar = {
                MessageInputBox(
                    modifier = Modifier.imePadding(),
                    textFieldState = messageTextFieldState,
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
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp)
            ) {
                if (state.isLoading && viewModel.messages.isEmpty()) {
                    ChatConversationShimmer(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(innerPadding)
                    )
                } else {
                    LazyColumn(
                        state = listState,
                        contentPadding = innerPadding,
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        reverseLayout = true
                    ) {
                        items(
                            items = viewModel.messages,
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
                                ChatPaginationShimmer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
