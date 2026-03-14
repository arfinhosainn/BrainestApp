package com.brainest.presentation.introduction.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import com.scelio.brainest.designsystem.BrainestSuccess
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.Typography
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SelectionOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    selectedColor: Color = BrainestSuccess,
    unselectedBorderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
    backgroundColor: Color = Color.White,
    selectedBackgroundColor: Color = Color.White,
    textStyle: TextStyle = Typography.bodyLarge,
    selectionId: String = label // Default to label, but can be customized for database storage
) {
    val animatedBackgroundColor by animateColorAsState(
        targetValue = if (isSelected) selectedBackgroundColor else backgroundColor,
        animationSpec = tween(durationMillis = 200),
        label = "background_color_animation"
    )

    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) selectedColor else unselectedBorderColor,
        animationSpec = tween(durationMillis = 200),
        label = "border_color_animation"
    )

    val borderWidth by animateDpAsState(
        targetValue = if (isSelected) 2.dp else 1.dp,
        animationSpec = tween(durationMillis = 200),
        label = "border_width_animation"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(borderWidth, animatedBorderColor),
        shadowElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leading Icon
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(16.dp))
            }

            // Label
            Text(
                text = label,
                style = textStyle,
                color = Color.Black,
                modifier = Modifier.weight(1f),

            )

            // Selection Indicator
            SelectionIndicator(
                isSelected = isSelected,
                selectedColor = selectedColor,
                unselectedBorderColor = unselectedBorderColor
            )
        }
    }
}

/**
 * A circular selection indicator that shows a checkmark when selected
 * or an empty circle when unselected.
 */
@Composable
private fun SelectionIndicator(
    isSelected: Boolean,
    selectedColor: Color,
    unselectedBorderColor: Color
) {
    val indicatorSize = 28.dp

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
                modifier = Modifier.size(18.dp)
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

// Previews
@Preview
@Composable
private fun SelectionOptionUnselectedPreview() {
    BrainestTheme {
        SelectionOption(
            label = "Younger than 6 months",
            isSelected = false,
            onClick = {},
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape)
                )
            }
        )
    }
}

@Preview
@Composable
private fun SelectionOptionSelectedPreview() {
    BrainestTheme {
        SelectionOption(
            label = "6 to 12 months",
            isSelected = true,
            onClick = {},
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape)
                )
            }
        )
    }
}

@Preview
@Composable
private fun SelectionOptionListPreview() {
    BrainestTheme {
        val options = listOf(
            "Younger than 6 months" to false,
            "6 to 12 months" to true,
            "1 to 2 years" to false,
            "2 to 7 years" to false,
            "Over 7 years" to false
        )

        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
        ) {
            options.forEach { (label, selected) ->
                SelectionOption(
                    label = label,
                    isSelected = selected,
                    onClick = {},
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.LightGray, CircleShape)
                        )
                    }
                )
            }
        }
    }
}
