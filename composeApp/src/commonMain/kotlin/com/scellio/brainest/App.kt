package com.scellio.brainest

import androidx.compose.runtime.Composable
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.presentation.register.RegisterRoot
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    BrainestTheme {
        RegisterRoot(onRegisterSuccess = {})

    }
}