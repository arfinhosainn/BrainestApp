package com.scelio.brainest.presentation.chat_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import brainest.feature.chat.presentation.generated.resources.Res
import brainest.feature.chat.presentation.generated.resources.improve_style
import com.scelio.brainest.designsystem.components.buttons.BrainestFloatingActionButton
import com.scelio.brainest.presentation.chat_list.components.ChatListHeader
import com.scelio.brainest.presentation.chat_list.components.ChatListItem
import com.scelio.brainest.presentation.components.SearchBar
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun ChatListRoot(
    onNavigateToChat: (String) -> Unit,
    viewModel: ChatListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is ChatListEvent.NavigateToChat -> onNavigateToChat(event.chatId)
                is ChatListEvent.ShowError -> snackbarHostState.showSnackbar(event.error.toString())
            }
        }
    }

    ChatListScreen(
        state = state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState
    )
}


@Composable
fun ChatListScreen(
    state: ChatListState,
    onAction: (ChatListAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val listState = rememberLazyListState()

    val hasChats = state.chats.isNotEmpty()


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            ChatListHeader(
                scrollState = listState,
                onSettingsClicked = { /* handle */ },
            )
        },
        floatingActionButton = {
            BrainestFloatingActionButton(
                onClick = { onAction(ChatListAction.OnFabClick) },
                content = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (hasChats || state.searchText.isNotEmpty()) {
                SearchBar(
                    searchQuery = state.searchText,
                    onSearchQueryChange = { query ->
                        onAction(ChatListAction.OnSearchQueryChange(query))
                    }
                )
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                if (state.chats.isEmpty() && state.searchText.isNotEmpty()) {
                    item {
                        Text(
                            text = "No conversations found for \"${state.searchText}\"",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    items(
                        items = state.chats,
                        key = { it.id }
                    ) { chat ->
                        ChatListItem(
                            chat = chat,
                            leadingIcon = painterResource(Res.drawable.improve_style),
                            onClick = { onAction(ChatListAction.OnChatClick(chat.id)) },
                            onDelete = { onAction(ChatListAction.OnDeleteChat(chat.id)) }
                        )
                    }
                }
            }
        }
    }
}






