package com.scelio.brainest.presentation.chat_detail

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scelio.brainest.domain.chat.ChatRepository
import com.scelio.brainest.domain.models.CreateChatRequest
import com.scelio.brainest.domain.models.SendMessageStreamEvent
import com.scelio.brainest.domain.models.SendMessageRequest
import com.scelio.brainest.presentation.chat_list.components.ChatSystemPrompt
import com.scelio.brainest.presentation.mappers.toUi
import com.scelio.brainest.presentation.model.ChatMessageUi
import com.scelio.brainest.presentation.util.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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
        loadRecentChats()
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

    @OptIn(ExperimentalUuidApi::class)
    private fun sendMessage() {
        val messageContent = messageTextFieldState.text.toString().trim()

        if (messageContent.isEmpty() || _state.value.isLoading) return

        viewModelScope.launch {
            val currentChatId = ensureChatExists() ?: return@launch
            updateChatTitleIfNeeded(currentChatId, messageContent)
            val tempUserMessageId = Uuid.random().toString()
            val tempAssistantMessageId = Uuid.random().toString()

            val userMessageUi = ChatMessageUi(
                id = tempUserMessageId,
                content = messageContent,
                isFromUser = true,
                timestamp = UiText.DynamicString("Just now"),
                isLoading = false
            )
            val assistantMessageUi = ChatMessageUi(
                id = tempAssistantMessageId,
                content = "",
                isFromUser = false,
                timestamp = UiText.DynamicString("Just now"),
                isLoading = true
            )

            _state.update {
                it.copy(
                    messages = listOf(assistantMessageUi, userMessageUi) + it.messages,
                    isLoading = true,
                    canSendMessage = false
                )
            }

            messageTextFieldState.clearText()
            var assistantPlaceholderIds = setOf(tempAssistantMessageId)

            try {
                val request = SendMessageRequest(
                    chatId = currentChatId,
                    userId = currentUserId,
                    content = messageContent
                )

                chatRepository.sendMessageStream(request).collect { event ->
                    when (event) {
                        is SendMessageStreamEvent.Started -> {
                            assistantPlaceholderIds =
                                assistantPlaceholderIds + event.assistantMessageId
                            _state.update { state ->
                                state.copy(
                                    messages = state.messages.map { message ->
                                        when (message.id) {
                                            tempUserMessageId -> event.userMessage.toUi()
                                            tempAssistantMessageId -> message.copy(id = event.assistantMessageId)
                                            else -> message
                                        }
                                    }
                                )
                            }
                        }

                        is SendMessageStreamEvent.AssistantPartial -> {
                            assistantPlaceholderIds =
                                assistantPlaceholderIds + event.messageId
                            _state.update { state ->
                                state.copy(
                                    messages = state.messages.map { message ->
                                        if (message.id == event.messageId) {
                                            message.copy(
                                                content = event.content,
                                                isLoading = false
                                            )
                                        } else {
                                            message
                                        }
                                    }
                                )
                            }
                        }

                        is SendMessageStreamEvent.Completed -> {
                            _state.update { state ->
                                state.copy(
                                    messages = state.messages.map { message ->
                                        if (message.id == event.message.id) {
                                            event.message.toUi()
                                        } else {
                                            message
                                        }
                                    },
                                    isLoading = false,
                                    canSendMessage = false
                                )
                            }
                        }
                    }
                }
                _events.send(ChatDetailEvent.OnNewMessage)
                loadRecentChats()

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        messages = it.messages.filter { msg ->
                            msg.id !in assistantPlaceholderIds
                        },
                        canSendMessage = true,
                        isLoading = false
                    )
                }
                _events.send(ChatDetailEvent.OnError(UiText.DynamicString(e.message ?: "Failed to send")))
            }
        }
    }

    private suspend fun ensureChatExists(): String? {
        _state.value.chatUi?.id?.let { return it }

        if (currentUserId.isBlank()) {
            _events.send(ChatDetailEvent.OnError(UiText.DynamicString("Not logged in")))
            return null
        }

        return try {
            val chat = chatRepository.createChat(
                CreateChatRequest(
                    userId = currentUserId,
                    title = "New chat",
                    systemPrompt = ChatSystemPrompt
                )
            )
            _state.update { it.copy(chatUi = chat.toUi()) }
            loadRecentChats()
            chat.id
        } catch (e: Exception) {
            _events.send(
                ChatDetailEvent.OnError(
                    UiText.DynamicString(e.message ?: "Failed to create chat")
                )
            )
            null
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
            it.copy(
                chatUi = null,
                isLoading = false,
                messages = emptyList(),
                error = null,
                canSendMessage = false,
                isPaginationLoading = false,
                paginationError = null,
                endReached = false,
                isNearBottom = false
            )
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

    private fun loadRecentChats() {
        if (currentUserId.isBlank()) return

        viewModelScope.launch {
            runCatching {
                chatRepository.getUserChats(currentUserId).map { it.toUi() }
            }.onSuccess { chats ->
                _state.update { it.copy(recentChats = chats) }
            }.onFailure { error ->
                _events.send(
                    ChatDetailEvent.OnError(
                        UiText.DynamicString(error.message ?: "Failed to load recent chats")
                    )
                )
            }
        }
    }

    private suspend fun updateChatTitleIfNeeded(chatId: String, messageContent: String) {
        val currentTitle = _state.value.chatUi?.title?.trim().orEmpty()
        if (currentTitle.isNotBlank() && !currentTitle.equals("New chat", ignoreCase = true)) {
            return
        }

        val generatedTitle = chatRepository.suggestChatTitle(messageContent)
            ?.let(::normalizeGeneratedTitle)
            ?: generateFallbackChatTitle(messageContent)
        if (generatedTitle.isBlank()) return

        runCatching {
            chatRepository.updateChatTitle(chatId, generatedTitle)
        }.onSuccess {
            _state.update { state ->
                state.copy(chatUi = state.chatUi?.copy(title = generatedTitle))
            }
            loadRecentChats()
        }
    }

    private fun generateFallbackChatTitle(messageContent: String): String {
        val firstLine = messageContent
            .lineSequence()
            .firstOrNull { it.isNotBlank() }
            .orEmpty()
            .replace(Regex("""[`*_#>\[\]\(\)]"""), " ")
            .replace(Regex("""\s+"""), " ")
            .trim()

        if (firstLine.isBlank()) return "Untitled chat"

        val withoutLeadIn = firstLine
            .replace(
                Regex(
                    pattern = """^(can you|could you|would you|please|help me|i need help with|explain|solve|show me|tell me about|what is|how do i|how to)\s+""",
                    option = RegexOption.IGNORE_CASE
                ),
                ""
            )
            .trim()
            .ifBlank { firstLine }

        val topicWords = withoutLeadIn
            .split(Regex("""\s+"""))
            .filter { it.isNotBlank() }
            .take(7)
            .mapIndexed { index, word ->
                val normalized = word.trim { it.isWhitespace() || it == ',' || it == '.' || it == '?' || it == '!' || it == ':' || it == ';' }
                if (normalized.isEmpty()) {
                    ""
                } else if (index == 0) {
                    normalized.replaceFirstChar { char -> char.titlecase() }
                } else {
                    normalized.lowercase()
                }
            }
            .filter { it.isNotBlank() }

        val title = topicWords.joinToString(" ").trim()
        if (title.isBlank()) return "Untitled chat"

        return if (title.length > 48) {
            title.take(45).trimEnd() + "..."
        } else {
            title
        }
    }

    private fun normalizeGeneratedTitle(title: String): String {
        val normalized = title
            .lineSequence()
            .firstOrNull { it.isNotBlank() }
            .orEmpty()
            .replace(Regex("""["'`]+"""), "")
            .replace(Regex("""[.!?,:;]+$"""), "")
            .replace(Regex("""\s+"""), " ")
            .trim()

        if (normalized.isBlank()) return ""

        return if (normalized.length > 48) {
            normalized.take(45).trimEnd() + "..."
        } else {
            normalized
        }
    }
}
