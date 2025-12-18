package com.scelio.brainest.presentation.chat_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scelio.brainest.presentation.chat_detail.components.MessageBox
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

    // Set user and load chat once
    LaunchedEffect(userId, chatId) {
        viewModel.setUserId(userId)
        viewModel.onAction(ChatDetailAction.OnSelectChat(chatId))
    }


    // Listen to one-off events (errors / new message)
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is ChatDetailEvent.OnError -> {
                    // Assuming you have UiText.asString(context)
                    snackbarHostState.showSnackbar(event.error.toString())
                }

                ChatDetailEvent.OnNewMessage -> {
                    // Optional: scroll handled below
                }
            }
        }
    }

    // Auto-scroll to the bottom when messages change (good for testing AI reply)
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.lastIndex)
        }
    }

    // Pagination: when user reaches the top, load older messages
    LaunchedEffect(listState) {
        snapshotFlow {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
        }.collectLatest { atTop ->
            if (atTop && state.messages.isNotEmpty()) {
                viewModel.onAction(ChatDetailAction.OnScrollToTop)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Messages list
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.messages) { msg ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (msg.isFromUser) Arrangement.End else Arrangement.Start
                    ) {
                        MessageBox(
                            message = msg.content,
                            isUserMessage = msg.isFromUser,
                            modifier = Modifier.widthIn(max = 280.dp)
                        )
                    }
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

            // Input row (uses the VM TextFieldState)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = viewModel.messageTextFieldState.text.toString(),
                    onValueChange = { newText ->
                        viewModel.messageTextFieldState.edit {
                            replace(0, length, newText)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text("Type a message") },
                    enabled = !state.isLoading
                )

                Spacer(Modifier.width(8.dp))

                Button(
                    onClick = { viewModel.onAction(ChatDetailAction.OnSendMessageClick) },
                    enabled = state.canSendMessage
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }

            if (state.isLoading) {
                Spacer(Modifier.height(8.dp))
                Text("Waiting for assistant...", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}