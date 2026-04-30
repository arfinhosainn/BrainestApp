package com.scelio.brainest.designsystem.components.navbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import brainest.core.designsystem.generated.resources.Res
import brainest.core.designsystem.generated.resources.ic_cards
import brainest.core.designsystem.generated.resources.ic_home
import brainest.core.designsystem.generated.resources.ic_scan
import brainest.core.designsystem.generated.resources.ic_stars
import com.scelio.brainest.designsystem.BrainestTheme
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

data class ButtonData(val text: String, val icon: ImageVector)

private val BarHeight = 72.dp

@Composable
fun BottomNavigationBar(
    buttons: List<ButtonData>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedColor = MaterialTheme.colorScheme.onSurface
    val unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .offset(y = (-2).dp)
            .padding(start = 20.dp, end = 20.dp, top = 2.dp, bottom = 0.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(40.dp),
            color = Color(0xFFF2F2F2),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(BarHeight)
                    .padding(horizontal = 1.dp, vertical = 1.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                buttons.forEachIndexed { index, button ->
                    val selected = index == selectedIndex
                    val interactionSource = remember { MutableInteractionSource() }
                    BoxWithConstraints(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = interactionSource,
                                indication = ripple(
                                    bounded = true,
                                    color = selectedColor,
                                    radius = 36.dp
                                ),
                                onClick = { onItemSelected(index) },
                            )
                            .padding(vertical = 0.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        val selectedBadgeSize = maxHeight - 6.dp
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Surface(
                                modifier = Modifier.size(if (selected) selectedBadgeSize else 44.dp),
                                shape = CircleShape,
                                color = if (selected) Color.White else Color.Transparent
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = button.icon,
                                        contentDescription = button.text,
                                        modifier = Modifier.size(if (selected) 28.dp else 26.dp),
                                        tint = if (selected) selectedColor else unselectedColor,
                                    )
                                }
                            }
                        }
                    }
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
            ButtonData("Scan", icon = vectorResource(Res.drawable.ic_scan)),
            ButtonData("Chat", icon = vectorResource(Res.drawable.ic_stars)),
            ButtonData("Flash&Quiz", icon = vectorResource(Res.drawable.ic_cards)),
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
