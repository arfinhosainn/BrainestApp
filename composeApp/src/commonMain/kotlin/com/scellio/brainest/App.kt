package com.scellio.brainest

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.presentation.register.RegisterRoot
import com.scellio.brainest.navigation.DeepLinkListener
import com.scellio.brainest.navigation.NavigationRoot
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    BrainestTheme {
        val navController = rememberNavController()
        DeepLinkListener(navController)
        BrainestTheme {
            NavigationRoot(navController)
        }

    }
}