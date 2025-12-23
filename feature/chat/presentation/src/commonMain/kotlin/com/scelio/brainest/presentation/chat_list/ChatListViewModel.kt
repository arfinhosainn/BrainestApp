package com.scelio.brainest.presentation.chat_list


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scelio.brainest.domain.auth.AuthService
import com.scelio.brainest.domain.chat.ChatRepository
import com.scelio.brainest.domain.models.CreateChatRequest
import com.scelio.brainest.presentation.mappers.toUi
import com.scelio.brainest.presentation.util.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChatListViewModel(
    private val chatRepository: ChatRepository,
    private val authService: AuthService
) : ViewModel() {

    private val _state = MutableStateFlow(ChatListState())
    val state = _state.asStateFlow()

    private val _events = Channel<ChatListEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadChats()
    }

    fun onAction(action: ChatListAction) {
        when (action) {
            ChatListAction.OnFabClick -> createChatAndNavigate()
            ChatListAction.OnRefresh -> loadChats()
            is ChatListAction.OnChatClick -> {
                viewModelScope.launch {
                    _events.send(ChatListEvent.NavigateToChat(action.chatId))
                }
            }

            else -> {}
        }
    }

    private fun loadChats() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val userId = authService.currentUserId()
            if (userId.isNullOrBlank()) {
                _state.update { it.copy(isLoading = false) }
                _events.send(ChatListEvent.ShowError(UiText.DynamicString("Not logged in")))
                return@launch
            }

            try {
                val chats = chatRepository.getUserChats(userId)
                    .map { it.chat.toUi() } // you return ChatWithLastMessage; adjust if you want last message too

                _state.update { it.copy(isLoading = false, chats = chats) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _events.send(
                    ChatListEvent.ShowError(
                        UiText.DynamicString(e.message ?: "Failed to load chats")
                    )
                )
            }
        }
    }

    private fun createChatAndNavigate() {
        viewModelScope.launch {
            if (_state.value.isCreatingChat) return@launch

            val userId = authService.currentUserId()
            if (userId.isNullOrBlank()) {
                _events.send(ChatListEvent.ShowError(UiText.DynamicString("Not logged in")))
                return@launch
            }

            _state.update { it.copy(isCreatingChat = true) }

            try {
                val chat = chatRepository.createChat(
                    CreateChatRequest(
                        userId = userId,
                        title = "New chat",
                        systemPrompt = ""
                    )
                )

                _state.update { it.copy(isCreatingChat = false) }
                _events.send(ChatListEvent.NavigateToChat(chat.id))
            } catch (e: Exception) {
                _state.update { it.copy(isCreatingChat = false) }
                _events.send(
                    ChatListEvent.ShowError(
                        UiText.DynamicString(e.message ?: "Failed to create chat")
                    )
                )
            }
        }
    }
}