package com.scellio.brainest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.graphics.toColorInt
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        val statusBarColor = "#1B5E3E".toColorInt()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                statusBarColor,
                statusBarColor
            ),
        )
        setContent {
            App()
        }
    }
}
