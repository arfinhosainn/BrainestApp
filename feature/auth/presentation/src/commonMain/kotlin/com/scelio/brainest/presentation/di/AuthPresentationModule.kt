package com.scelio.brainest.presentation.di

import com.scelio.brainest.presentation.email_verification.EmailVerificationViewModel
import com.scelio.brainest.presentation.forgot_password.ForgotPasswordViewModel
import com.scelio.brainest.presentation.login.LoginViewModel
import com.scelio.brainest.presentation.register.RegisterViewModel
import com.scelio.brainest.presentation.register_success.RegisterSuccessViewModel
import com.scelio.brainest.presentation.reset_password.ResetPasswordViewModel
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModelOf


val authPresentationModule = module {
    viewModelOf(::RegisterViewModel)
    viewModelOf(::RegisterSuccessViewModel)
    viewModelOf(::EmailVerificationViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::ForgotPasswordViewModel)
    viewModelOf(::ResetPasswordViewModel)

}