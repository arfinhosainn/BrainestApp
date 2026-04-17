package com.scelio.brainest.presentation.chat_list


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scelio.brainest.domain.auth.AuthService
import com.scelio.brainest.domain.chat.ChatRepository
import com.scelio.brainest.domain.models.CreateChatRequest
import com.scelio.brainest.presentation.chat_list.components.ChatSystemPrompt
import com.scelio.brainest.presentation.mappers.toUi
import com.scelio.brainest.presentation.util.UiText
import brainest.feature.chat.presentation.generated.resources.Res
import brainest.feature.chat.presentation.generated.resources.failed_to_create_chat
import brainest.feature.chat.presentation.generated.resources.failed_to_delete_chat
import brainest.feature.chat.presentation.generated.resources.failed_to_load_chat
import brainest.feature.chat.presentation.generated.resources.failed_to_search_chats
import brainest.feature.chat.presentation.generated.resources.new_chat
import brainest.feature.chat.presentation.generated.resources.not_logged_in
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString



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

            is ChatListAction.OnDeleteChat -> {
                deleteChat(action.chatId)

            }

            is ChatListAction.OnSearchQueryChange -> {
                updateSearchQuery(action.query)

            }
        }
    }


    private fun updateSearchQuery(query: String) {
        _state.update { it.copy(searchText = query) }

        viewModelScope.launch {
            val userId = authService.currentUserId()
            if (userId.isNullOrBlank()) return@launch

            try {
                val chats = if (query.isBlank()) {
                    chatRepository.getUserChats(userId)
                } else {
                    chatRepository.searchChats(userId, query)
                }.map { it.chat.toUi() }

                _state.update { it.copy(chats = chats) }
            } catch (e: Exception) {
                _events.send(
                    ChatListEvent.ShowError(
                        e.message?.let { UiText.DynamicString(it) } ?: UiText.Resource(Res.string.failed_to_search_chats)
                    )
                )
            }
        }
    }


    private fun deleteChat(chatId: String) {
        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(chats = it.chats.filter { chat -> chat.id != chatId })
                }
                chatRepository.deleteChat(chatId)

                val userId = authService.currentUserId()
                if (!userId.isNullOrBlank()) {
                    val chats = chatRepository.getUserChats(userId)
                        .map { it.chat.toUi() }
                    _state.update { it.copy(chats = chats) }
                }
            } catch (e: Exception) {
                loadChats()
                _events.send(
                    ChatListEvent.ShowError(
                        e.message?.let { UiText.DynamicString(it) } ?: UiText.Resource(Res.string.failed_to_delete_chat)
                    )
                )
            }
        }
    }


    private fun loadChats() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val userId = authService.currentUserId()
            if (userId.isNullOrBlank()) {
                _state.update { it.copy(isLoading = false) }
                _events.send(ChatListEvent.ShowError(UiText.Resource(Res.string.not_logged_in)))
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
                        e.message?.let { UiText.DynamicString(it) } ?: UiText.Resource(Res.string.failed_to_load_chat)
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
                _events.send(ChatListEvent.ShowError(UiText.Resource(Res.string.not_logged_in)))
                return@launch
            }

            _state.update { it.copy(isCreatingChat = true) }

            try {
                val chat = chatRepository.createChat(
                    CreateChatRequest(
                        userId = userId,
                        title = getString(Res.string.new_chat),
                        systemPrompt = ChatSystemPrompt
                    )
                )

                _state.update { it.copy(isCreatingChat = false) }
                _events.send(ChatListEvent.NavigateToChat(chat.id))
            } catch (e: Exception) {
                _state.update { it.copy(isCreatingChat = false) }
                _events.send(
                    ChatListEvent.ShowError(
                        e.message?.let { UiText.DynamicString(it) } ?: UiText.Resource(Res.string.failed_to_create_chat)
                    )
                )
            }
        }
    }
}
