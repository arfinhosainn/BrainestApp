package com.scellio.brainest

import android.os.Bundle
import android.graphics.Color as AndroidColor
import android.os.Build
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : AppCompatActivity() {
    @Volatile
    private var keepSplashScreenVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplashScreenVisible }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        window.statusBarColor = AndroidColor.TRANSPARENT
        window.navigationBarColor = AndroidColor.TRANSPARENT
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                AndroidColor.TRANSPARENT,
                AndroidColor.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                AndroidColor.TRANSPARENT,
                AndroidColor.TRANSPARENT
            )
        )
        setContent {
            App(
                onAutheticationChecked = {
                    keepSplashScreenVisible = false
                }
            )
        }
    }
}
