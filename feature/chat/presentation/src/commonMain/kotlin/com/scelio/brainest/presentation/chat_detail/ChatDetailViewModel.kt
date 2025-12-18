package com.scelio.brainest.presentation.chat_detail

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scelio.brainest.domain.chat.ChatRepository
import com.scelio.brainest.domain.models.SendMessageRequest
import com.scelio.brainest.presentation.mappers.toUi
import com.scelio.brainest.presentation.util.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChatDetailViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChatDetailState())
    val state = _state.asStateFlow()

    private val _events = Channel<ChatDetailEvent>()
    val events = _events.receiveAsFlow()

    val messageTextFieldState = TextFieldState()

    private var currentUserId: String = ""
    private val pageSize = 20
    private var currentPage = 0

    init {
        viewModelScope.launch {
            snapshotFlow { messageTextFieldState.text.toString() }
                .collect { text ->
                    _state.update {
                        it.copy(canSendMessage = text.trim().isNotEmpty() && !it.isLoading)
                    }
                }
        }
    }

    fun onAction(action: ChatDetailAction) {
        when (action) {
            is ChatDetailAction.OnSelectChat -> selectChat(action.chatId)
            is ChatDetailAction.OnSendMessageClick -> sendMessage()
            is ChatDetailAction.OnScrollToTop -> loadMoreMessages()
            is ChatDetailAction.OnBackClick -> clearSelectedChat()
            is ChatDetailAction.OnDismissMessageMenu -> dismissMessageMenu()
            is ChatDetailAction.OnRetryPaginationClick -> retryPagination()
        }
    }

    fun setUserId(userId: String) {
        currentUserId = userId
    }

    private fun selectChat(chatId: String?) {
        if (chatId == null) {
            clearSelectedChat()
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    messages = emptyList(),
                    endReached = false
                )
            }

            try {
                val chat = chatRepository.getChat(chatId)
                if (chat == null) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = UiText.DynamicString("Chat not found")
                        )
                    }
                    _events.send(
                        ChatDetailEvent.OnError(UiText.DynamicString("Chat not found"))
                    )
                    return@launch
                }

                val history = chatRepository.getChatHistory(chatId)
                val messages = history.messages
                    .takeLast(pageSize)
                    .map { it.toUi() }
                    .reversed() // Most recent at bottom

                _state.update {
                    it.copy(
                        chatUi = chat.toUi(),
                        messages = messages,
                        isLoading = false,
                        endReached = history.messages.size <= pageSize
                    )
                }

                currentPage = 0

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = UiText.DynamicString(
                            e.message ?: "Failed to load chat"
                        )
                    )
                }
                _events.send(
                    ChatDetailEvent.OnError(
                        UiText.DynamicString(e.message ?: "Failed to load chat")
                    )
                )
            }
        }
    }

    private fun sendMessage() {
        val currentChatId = _state.value.chatUi?.id ?: return
        val messageContent = messageTextFieldState.text.toString().trim()

        if (messageContent.isEmpty() || _state.value.isLoading) return

        viewModelScope.launch {
            _state.update { it.copy(canSendMessage = false, isLoading = true) }

            try {
                messageTextFieldState.clearText()

                val request = SendMessageRequest(
                    chatId = currentChatId,
                    userId = currentUserId,
                    content = messageContent
                )

                val assistantMessage = chatRepository.sendMessage(request)

                val history = chatRepository.getChatHistory(currentChatId)
                val updatedMessages = history.messages
                    .takeLast(pageSize * (currentPage + 1))
                    .map { it.toUi() }
                    .reversed()

                _state.update {
                    it.copy(
                        messages = updatedMessages,
                        isLoading = false
                    )
                }

                _events.send(ChatDetailEvent.OnNewMessage)

            } catch (e: Exception) {
                messageTextFieldState.edit {
                    append(messageContent)
                }

                _state.update {
                    it.copy(
                        canSendMessage = true,
                        isLoading = false
                    )
                }

                _events.send(
                    ChatDetailEvent.OnError(
                        UiText.DynamicString(e.message ?: "Failed to send message")
                    )
                )
            }
        }
    }

    private fun loadMoreMessages() {
        val currentChatId = _state.value.chatUi?.id ?: return

        if (_state.value.isPaginationLoading || _state.value.endReached) {
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isPaginationLoading = true,
                    paginationError = null
                )
            }

            try {
                val history = chatRepository.getChatHistory(currentChatId)
                val nextPage = currentPage + 1
                val startIndex = pageSize * nextPage
                val endIndex = startIndex + pageSize

                if (startIndex >= history.messages.size) {
                    // No more messages
                    _state.update {
                        it.copy(
                            isPaginationLoading = false,
                            endReached = true
                        )
                    }
                    return@launch
                }

                val olderMessages = history.messages
                    .reversed() // Oldest to newest
                    .drop(startIndex)
                    .take(pageSize)
                    .map { it.toUi() }

                _state.update {
                    it.copy(
                        messages = olderMessages + it.messages,
                        isPaginationLoading = false,
                        endReached = endIndex >= history.messages.size
                    )
                }

                currentPage = nextPage

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isPaginationLoading = false,
                        paginationError = UiText.DynamicString(
                            e.message ?: "Failed to load more messages"
                        )
                    )
                }
            }
        }
    }

    private fun retryPagination() {
        if (_state.value.paginationError != null) {
            loadMoreMessages()
        }
    }

    private fun clearSelectedChat() {
        _state.update {
            ChatDetailState()
        }
        messageTextFieldState.clearText()
        currentPage = 0
    }

    private fun dismissMessageMenu() {
        // Implement if you have a message context menu
        // For now, this is a no-op
    }

    fun updateScrollPosition(isNearBottom: Boolean) {
        _state.update { it.copy(isNearBottom = isNearBottom) }
    }
}