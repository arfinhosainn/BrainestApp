package com.scelio.brainest.designsystem.components.navbar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import brainest.core.designsystem.generated.resources.Res
import brainest.core.designsystem.generated.resources.ic_chat
import brainest.core.designsystem.generated.resources.ic_flash
import brainest.core.designsystem.generated.resources.ic_home
import brainest.core.designsystem.generated.resources.ic_scan
import brainest.core.designsystem.generated.resources.ic_settings
import com.scelio.brainest.designsystem.BrainestBase700
import com.scelio.brainest.designsystem.BrainestMath
import com.scelio.brainest.designsystem.BrainestTheme
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

data class ButtonData(val text: String, val icon: ImageVector)

private val FabSize = 50.dp
private val FabElevation = 0.dp
private val BarHeight = 72.dp

@Composable
fun BottomNavigationBar(
    buttons: List<ButtonData>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedColor = BrainestMath
    val unselectedColor = BrainestBase700
    val fabIndex = buttons.size / 2

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(BarHeight + FabSize / 2),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(BarHeight)
                .align(Alignment.BottomCenter)
                .background(MaterialTheme.colorScheme.surface),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                buttons.forEachIndexed { index, button ->
                    if (index == fabIndex) {
                        Spacer(modifier = Modifier.weight(1f))
                        return@forEachIndexed
                    }
                    val selected = index == selectedIndex
                    val interactionSource = remember { MutableInteractionSource() }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = ripple(bounded = true, color = selectedColor, radius = 36.dp),
                                onClick = { onItemSelected(index) },
                            )
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Icon(
                                imageVector = button.icon,
                                contentDescription = button.text,
                                modifier = Modifier.size(24.dp),
                                tint = if (selected) selectedColor else unselectedColor,
                            )
                            Text(
                                text = button.text,
                                color = if (selected) selectedColor else unselectedColor,
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                }
            }
        }

        val fabButton = buttons[fabIndex]
        val fabSelected = fabIndex == selectedIndex
        val fabInteractionSource = remember { MutableInteractionSource() }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 30.dp)
                .size(FabSize)
                .shadow(elevation = FabElevation, shape = CircleShape, clip = false)
                .clip(CircleShape)
                .background(Color.Black)
                .clickable(
                    interactionSource = fabInteractionSource,
                    indication = ripple(bounded = true, color = Color.White),
                    onClick = { onItemSelected(fabIndex) },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = fabButton.icon,
                contentDescription = fabButton.text,
                modifier = Modifier.size(24.dp),
                tint = if (fabSelected) selectedColor else Color.White,
            )
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
            ButtonData("Flash&Quiz", icon = vectorResource(Res.drawable.ic_flash)),
            ButtonData("Settings", icon = vectorResource(Res.drawable.ic_settings)),
        )

        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.weight(1f))
                BottomNavigationBar(
                    buttons = buttons,
                    selectedIndex = selectedIndex,
                    onItemSelected = { selectedIndex = it },
                )
            }
        }
    }
}