package com.scelio.brainest.presentation.chat_detail

sealed interface ChatDetailAction {
    data object OnSendMessageClick: ChatDetailAction
    data object OnScrollToTop: ChatDetailAction
    data class OnSelectChat(val chatId: String?): ChatDetailAction
    data object OnDismissMessageMenu: ChatDetailAction
    data object OnBackClick: ChatDetailAction
    data object OnRetryPaginationClick: ChatDetailAction
}