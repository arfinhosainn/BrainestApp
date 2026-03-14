package com.brainest.presentation.introduction.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scelio.brainest.designsystem.BrainestSuccess
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.Typography
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MultiSelectChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedColor: Color = BrainestSuccess,
    unselectedBorderColor: Color = Color(0xFF2C201F).copy(alpha = 0.5f),
    backgroundColor: Color = Color.Transparent,
    selectedBackgroundColor: Color = Color.White,
    textStyle: TextStyle = Typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
    selectionId: String = label
) {
    val animatedBackgroundColor by animateColorAsState(
        targetValue = if (isSelected) selectedBackgroundColor else backgroundColor,
        animationSpec = tween(durationMillis = 200),
        label = "multiselect_background_animation"
    )

    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) selectedColor else unselectedBorderColor,
        animationSpec = tween(durationMillis = 200),
        label = "multiselect_border_animation"
    )

    val animatedContentColor by animateColorAsState(
        targetValue = if (isSelected) Color.Black else Color.Black,
        animationSpec = tween(durationMillis = 200),
        label = "multiselect_content_animation"
    )

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        color = animatedBackgroundColor,
        border = BorderStroke(1.dp, animatedBorderColor)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Label
            Text(
                text = label,
                style = textStyle,
                color = animatedContentColor
            )

            // Circular Check Indicator
            SelectionIndicator(
                isSelected = isSelected,
                selectedColor = selectedColor,
                unselectedBorderColor = unselectedBorderColor
            )
        }
    }
}

@Composable
private fun SelectionIndicator(
    isSelected: Boolean,
    selectedColor: Color,
    unselectedBorderColor: Color
) {
    val indicatorSize = 24.dp

    if (isSelected) {
        Box(
            modifier = Modifier
                .size(indicatorSize)
                .clip(CircleShape)
                .background(selectedColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    } else {
        Box(
            modifier = Modifier
                .size(indicatorSize)
                .clip(CircleShape)
                .border(
                    width = 1.5.dp,
                    color = unselectedBorderColor,
                    shape = CircleShape
                )
        )
    }
}

@Preview
@Composable
private fun MultiSelectChipUnselectedPreview() {
    BrainestTheme {
        MultiSelectChip(
            label = "Name",
            isSelected = false,
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun MultiSelectChipSelectedPreview() {
    BrainestTheme {
        MultiSelectChip(
            label = "Come",
            isSelected = true,
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun MultiSelectChipFlowPreview() {
    BrainestTheme {
        val commands = listOf(
            "Name" to false,
            "Stand" to false,
            "Sit" to false,
            "Down" to false,
            "Leave it" to false,
            "Come" to true,
            "Stay" to false,
            "High Five" to false
        )

        androidx.compose.foundation.layout.FlowRow(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            commands.forEach { (command, selected) ->
                MultiSelectChip(
                    label = command,
                    isSelected = selected,
                    onClick = {}
                )
            }
        }
    }
}
