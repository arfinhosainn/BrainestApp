package com.scellio.brainest.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import brainest.composeapp.generated.resources.Res
import brainest.composeapp.generated.resources.bottom_nav_chat
import brainest.composeapp.generated.resources.bottom_nav_flash_quiz
import brainest.composeapp.generated.resources.bottom_nav_home
import brainest.composeapp.generated.resources.ic_cards
import brainest.composeapp.generated.resources.ic_home
import brainest.composeapp.generated.resources.ic_stars
import com.scelio.brainest.designsystem.components.navbar.BottomNavigationBar
import com.scelio.brainest.designsystem.components.navbar.ButtonData
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun BrainestBottomNavigationBar(
    modifier: Modifier = Modifier,
    onItemSelected: (Int) -> Unit = {}
) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

    val buttons = listOf(
        ButtonData(stringResource(Res.string.bottom_nav_home), icon = vectorResource(Res.drawable.ic_home)),
        ButtonData(stringResource(Res.string.bottom_nav_chat), icon = vectorResource(Res.drawable.ic_stars)),
        ButtonData(stringResource(Res.string.bottom_nav_flash_quiz), icon = vectorResource(Res.drawable.ic_cards)),
    )

    BottomNavigationBar(
        buttons = buttons,
        selectedIndex = selectedIndex,
        onItemSelected = {
            selectedIndex = it
            onItemSelected(it)
        },
        modifier = modifier,
    )
}
