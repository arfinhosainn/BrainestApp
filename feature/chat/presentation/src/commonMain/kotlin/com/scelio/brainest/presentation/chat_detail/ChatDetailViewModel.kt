package com.scelio.brainest.presentation.chat_detail

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import brainest.feature.chat.presentation.generated.resources.Res
import brainest.feature.chat.presentation.generated.resources.chat_not_found
import brainest.feature.chat.presentation.generated.resources.failed_to_create_chat
import brainest.feature.chat.presentation.generated.resources.failed_to_load_chat
import brainest.feature.chat.presentation.generated.resources.failed_to_load_more_messages
import brainest.feature.chat.presentation.generated.resources.failed_to_send
import brainest.feature.chat.presentation.generated.resources.just_now
import brainest.feature.chat.presentation.generated.resources.new_chat
import brainest.feature.chat.presentation.generated.resources.not_logged_in
import com.scelio.brainest.domain.chat.ChatRepository
import com.scelio.brainest.domain.chat.generateFallbackChatTitle
import com.scelio.brainest.domain.chat.normalizeGeneratedTitle
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
import kotlinx.coroutines.sync.Mutex
import org.jetbrains.compose.resources.getString
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ChatDetailViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChatDetailState())
    val state = _state.asStateFlow()

    // Separate SnapshotStateList for messages to avoid full list replacement on updates
    private val _messages = mutableStateListOf<ChatMessageUi>()
    val messages: List<ChatMessageUi> = _messages

    private val _events = Channel<ChatDetailEvent>()
    val events = _events.receiveAsFlow()

    private var currentUserId: String = ""
    private val pageSize = 20
    private var currentPage = 0
    private val sendMessageMutex = Mutex()

    fun onAction(action: ChatDetailAction) {
        when (action) {
            is ChatDetailAction.OnSelectChat -> selectChat(action.chatId)
            is ChatDetailAction.OnSendMessageClick -> sendMessage()
            is ChatDetailAction.OnScrollToTop -> loadMoreMessages()
            is ChatDetailAction.OnBackClick -> clearSelectedChat()
            is ChatDetailAction.OnDismissMessageMenu -> dismissMessageMenu()
            is ChatDetailAction.OnRetryPaginationClick -> retryPagination()
            is ChatDetailAction.OnMessageTextChanged -> onMessageTextChanged(action.text)
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
                    endReached = false
                )
            }
            _messages.clear()

            try {
                val chat = chatRepository.getChat(chatId)
                if (chat == null) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = UiText.Resource(Res.string.chat_not_found)
                        )
                    }
                    _events.send(
                        ChatDetailEvent.OnError(UiText.Resource(Res.string.chat_not_found))
                    )
                    return@launch
                }

                val history = chatRepository.getChatHistory(chatId)
                val loadedMessages = history.messages
                    .takeLast(pageSize)
                    .map { it.toUi() }
                    .reversed() // Most recent at bottom

                _messages.addAll(loadedMessages)
                _state.update {
                    it.copy(
                        chatUi = chat.toUi(),
                        isLoading = false,
                        endReached = history.messages.size <= pageSize
                    )
                }

                currentPage = 0

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message?.let { UiText.DynamicString(it) } ?: UiText.Resource(Res.string.failed_to_load_chat)
                    )
                }
                _events.send(
                    ChatDetailEvent.OnError(
                        e.message?.let { UiText.DynamicString(it) } ?: UiText.Resource(Res.string.failed_to_load_chat)
                    )
                )
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun sendMessage() {
        val messageContent = _state.value.messageText.trim()

        if (messageContent.isEmpty()) return

        viewModelScope.launch {
            if (!sendMessageMutex.tryLock()) return@launch

            try {
                _state.update {
                    it.copy(
                        isLoading = true,
                        canSendMessage = false,
                        error = null
                    )
                }

                val currentChatId = ensureChatExists() ?: run {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            canSendMessage = it.messageText.trim().isNotEmpty()
                        )
                    }
                    return@launch
                }

                updateChatTitleIfNeeded(currentChatId, messageContent)
                val tempUserMessageId = Uuid.random().toString()
                val tempAssistantMessageId = Uuid.random().toString()

                val userMessageUi = ChatMessageUi(
                    id = tempUserMessageId,
                    content = messageContent,
                    isFromUser = true,
                    timestamp = UiText.Resource(Res.string.just_now),
                    isLoading = false
                )
                val assistantMessageUi = ChatMessageUi(
                    id = tempAssistantMessageId,
                    content = "",
                    isFromUser = false,
                    timestamp = UiText.Resource(Res.string.just_now),
                    isLoading = true
                )

                // Add messages to the front of the list (most recent first)
                _messages.add(0, userMessageUi)
                _messages.add(0, assistantMessageUi)
                _state.update {
                    it.copy(
                        isLoading = true,
                        canSendMessage = false,
                        messageText = ""
                    )
                }
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
                                // Update user message in place
                                val userIndex = _messages.indexOfFirst { it.id == tempUserMessageId }
                                if (userIndex != -1) {
                                    _messages[userIndex] = event.userMessage.toUi()
                                }
                                // Update assistant message id in place
                                val assistantIndex = _messages.indexOfFirst { it.id == tempAssistantMessageId }
                                if (assistantIndex != -1) {
                                    _messages[assistantIndex] = _messages[assistantIndex].copy(id = event.assistantMessageId)
                                }
                            }

                            is SendMessageStreamEvent.AssistantPartial -> {
                                assistantPlaceholderIds =
                                    assistantPlaceholderIds + event.messageId
                                // Update only the specific message being streamed
                                val index = _messages.indexOfFirst { it.id == event.messageId }
                                if (index != -1) {
                                    _messages[index] = _messages[index].copy(
                                        content = event.content,
                                        isLoading = false
                                    )
                                }
                            }

                            is SendMessageStreamEvent.Completed -> {
                                // Update only the completed message
                                val index = _messages.indexOfFirst { it.id == event.message.id }
                                if (index != -1) {
                                    _messages[index] = event.message.toUi()
                                }
                                _state.update {
                                    it.copy(
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
                    // Remove failed messages from the list
                    _messages.removeAll { it.id in assistantPlaceholderIds }
                    _state.update {
                        it.copy(
                            canSendMessage = true,
                            isLoading = false
                        )
                    }
                    _events.send(ChatDetailEvent.OnError(e.message?.let { UiText.DynamicString(it) } ?: UiText.Resource(Res.string.failed_to_send)))
                }
            } finally {
                sendMessageMutex.unlock()
            }
        }
    }

    private suspend fun ensureChatExists(): String? {
        _state.value.chatUi?.id?.let { return it }

        if (currentUserId.isBlank()) {
            _events.send(ChatDetailEvent.OnError(UiText.Resource(Res.string.not_logged_in)))
            return null
        }

        return try {
            val chat = chatRepository.createChat(
                CreateChatRequest(
                    userId = currentUserId,
                    title = getString(Res.string.new_chat),
                    systemPrompt = ChatSystemPrompt
                )
            )
            _state.update { it.copy(chatUi = chat.toUi()) }
            loadRecentChats()
            chat.id
        } catch (e: Exception) {
            _events.send(
                ChatDetailEvent.OnError(
                    e.message?.let { UiText.DynamicString(it) } ?: UiText.Resource(Res.string.failed_to_create_chat)
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

                // Prepend older messages to the front of the list
                _messages.addAll(0, olderMessages)
                _state.update {
                    it.copy(
                        isPaginationLoading = false,
                        endReached = endIndex >= history.messages.size
                    )
                }

                currentPage = nextPage

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isPaginationLoading = false,
                        paginationError = e.message?.let { UiText.DynamicString(it) } ?: UiText.Resource(Res.string.failed_to_load_more_messages)
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
        _messages.clear()
        _state.update {
            it.copy(
                chatUi = null,
                isLoading = false,
                error = null,
                canSendMessage = false,
                isPaginationLoading = false,
                paginationError = null,
                endReached = false,
                isNearBottom = false,
                messageText = ""
            )
        }
        currentPage = 0
    }

    private fun onMessageTextChanged(text: String) {
        _state.update {
            it.copy(
                messageText = text,
                canSendMessage = text.trim().isNotEmpty() && !it.isLoading
            )
        }
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
                        error.message?.let { UiText.DynamicString(error.message ?: "") } ?: UiText.Resource(Res.string.failed_to_load_chat)
                    )
                )
            }
        }
    }

    private suspend fun updateChatTitleIfNeeded(chatId: String, messageContent: String) {
        val currentTitle = _state.value.chatUi?.title?.trim().orEmpty()
        val defaultNewChatTitle = getString(Res.string.new_chat)
        if (currentTitle.isNotBlank() && !currentTitle.equals(defaultNewChatTitle, ignoreCase = true)) {
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
}
