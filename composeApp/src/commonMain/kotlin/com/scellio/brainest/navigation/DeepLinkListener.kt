package com.scellio.brainest.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.NavController
import com.scelio.brainest.presentation.navigation.AuthGraphRoutes

@Composable
fun DeepLinkListener(
    navController: NavController
) {
    DisposableEffect(Unit) {
        ExternalUriHandler.listener = { uri ->
            when {
                uri.contains("/auth/verify") -> {
                    navController.navigate(
                        AuthGraphRoutes.EmailVerification(deepLinkUrl = uri)
                    )
                }
                uri.contains("/auth/reset-password") -> {
                    navController.navigate(
                        AuthGraphRoutes.ResetPassword(deepLinkUrl = uri)
                    )
                }
            }
        }
        onDispose {
            ExternalUriHandler.listener = null
        }
    }
}


