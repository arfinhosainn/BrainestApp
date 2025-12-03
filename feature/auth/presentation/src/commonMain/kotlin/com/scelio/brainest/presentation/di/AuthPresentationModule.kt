package com.scelio.brainest.presentation.di

import com.scelio.brainest.presentation.email_verification.EmailVerificationViewModel
import com.scelio.brainest.presentation.register.RegisterViewModel
import com.scelio.brainest.presentation.register_success.RegisterSuccessViewModel
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModelOf


val authPresentationModule = module {
    viewModelOf(::RegisterViewModel)
    viewModelOf(::RegisterSuccessViewModel)
    viewModelOf(::EmailVerificationViewModel)
}