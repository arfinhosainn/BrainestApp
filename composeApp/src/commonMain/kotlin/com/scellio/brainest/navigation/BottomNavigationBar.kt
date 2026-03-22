package com.scellio.brainest.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import brainest.composeapp.generated.resources.Res
import brainest.composeapp.generated.resources.ic_chat
import brainest.composeapp.generated.resources.ic_flash
import brainest.composeapp.generated.resources.ic_home
import brainest.composeapp.generated.resources.ic_scan
import brainest.composeapp.generated.resources.ic_settings
import com.scelio.brainest.designsystem.components.navbar.ButtonData
import com.scelio.brainest.designsystem.components.navbar.BottomNavigationBar
import org.jetbrains.compose.resources.vectorResource

@Composable
fun BrainestBottomNavigationBar(
    modifier: Modifier = Modifier
) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

    val buttons = listOf(
        ButtonData("Home", icon = vectorResource(Res.drawable.ic_home)),
        ButtonData("Chat", icon = vectorResource(Res.drawable.ic_chat)),
        ButtonData("Scan", icon = vectorResource(Res.drawable.ic_scan)),
        ButtonData("Flash&Quiz", icon = vectorResource(Res.drawable.ic_flash)),
        ButtonData("Settings", icon = vectorResource(Res.drawable.ic_settings)),
    )

    BottomNavigationBar(
        buttons = buttons,
        selectedIndex = selectedIndex,
        onItemSelected = { selectedIndex = it },
        modifier = modifier,
    )
}
