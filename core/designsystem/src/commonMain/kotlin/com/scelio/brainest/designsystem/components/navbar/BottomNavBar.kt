package com.scelio.brainest.designsystem.components.navbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import brainest.core.designsystem.generated.resources.Res
import brainest.core.designsystem.generated.resources.ic_chat
import brainest.core.designsystem.generated.resources.ic_home
import brainest.core.designsystem.generated.resources.ic_scan
import brainest.core.designsystem.generated.resources.ic_settings
import com.scelio.brainest.designsystem.BrainestBase700
import com.scelio.brainest.designsystem.BrainestMath
import com.scelio.brainest.designsystem.BrainestTheme
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

data class ButtonData(val text: String, val icon: ImageVector)

@Composable
fun SimpleNavigationBar(
    buttons: List<ButtonData>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedColor = BrainestMath
    val unselectedColor = BrainestBase700

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        buttons.forEachIndexed { index, button ->
            val selected = index == selectedIndex
            val interactionSource = remember { MutableInteractionSource() }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = ripple(
                            bounded = true,
                            color = selectedColor,
                            radius = 36.dp
                        )
                    ) { onItemSelected(index) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        button.icon,
                        contentDescription = button.text,
                        modifier = Modifier.size(28.dp),
                        tint = if (selected) selectedColor else unselectedColor
                    )
                    Text(
                        button.text,
                        color = if (selected) selectedColor else unselectedColor,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewSimpleNavigationBar() {
    BrainestTheme {
        var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
        val buttons = listOf(
            ButtonData("Home", icon = vectorResource(Res.drawable.ic_home)),
            ButtonData("Chat", icon = vectorResource(Res.drawable.ic_chat)),
            ButtonData("Scan", icon = vectorResource(Res.drawable.ic_scan)),
            ButtonData("Flash&Quiz", Icons.Default.DateRange),
            ButtonData("Settings", icon = vectorResource(Res.drawable.ic_settings)),
        )
        SimpleNavigationBar(
            buttons = buttons,
            selectedIndex = selectedIndex,
            onItemSelected = { selectedIndex = it }
        )
    }
}
