package com.scelio.brainest.presentation.di

import com.scelio.brainest.presentation.chat_list.ChatListViewModel
import com.scelio.brainest.presentation.chat_detail.ChatDetailViewModel
import com.scelio.brainest.presentation.create_chat.CreateChatViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val chatPresentationModule = module {
    viewModelOf(::ChatListViewModel)
    viewModelOf(::ChatDetailViewModel)
    viewModelOf(::CreateChatViewModel)
}