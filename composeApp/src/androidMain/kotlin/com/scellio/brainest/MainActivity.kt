package com.scellio.brainest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.graphics.toColorInt

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
