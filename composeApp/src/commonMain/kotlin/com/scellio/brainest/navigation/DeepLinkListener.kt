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
            val encodedUri = uri.encodeUrl()
            navController.navigate(AuthGraphRoutes.EmailVerification(deepLinkUrl = encodedUri))
        }
        onDispose {
            ExternalUriHandler.listener = null
        }
    }
}

private fun String.encodeUrl(): String {
    return this.map { char ->
        when {
            char.isLetterOrDigit() -> char.toString()
            char in "-_.~" -> char.toString()
            else -> "%${char.code.toString(16).uppercase().padStart(2, '0')}"
        }
    }.joinToString("")
}