package com.scelio.brainest.presentation.chat_list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.components.buttons.BrainestFloatingActionButton
import com.scelio.brainest.presentation.chat_list.components.ChatListHeader
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChatListScreen(
    state: ChatListState,
    onAction: (ChatListAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val listState = rememberLazyListState() // Create list state

    Scaffold(
        topBar = {
            ChatListHeader(
                scrollState = listState, // Pass scroll state
                onSettingsClicked = { /* handle */ },
            )
        },
        floatingActionButton = {
            BrainestFloatingActionButton(
                onClick = {},
                content = {})
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->
        LazyColumn(
            state = listState, // Use same state
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(100) { index ->
                Text(index.toString())

            }
        }
    }
}


@Preview
@Composable
fun PreviewChatListScreen() {


    BrainestTheme(darkTheme = true) {
        ChatListScreen(
            state = ChatListState(),
            onAction = {},
            snackbarHostState = SnackbarHostState()
        )
    }

}

