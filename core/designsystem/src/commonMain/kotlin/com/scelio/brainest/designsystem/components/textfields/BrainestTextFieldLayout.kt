package com.scelio.brainest.designsystem.components.textfields

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scelio.brainest.designsystem.extended

@Composable
internal fun rememberTextFieldInteraction(
    onFocusChanged: (Boolean) -> Unit
): TextFieldInteraction {
    val interactionSource = androidx.compose.runtime.remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    
    LaunchedEffect(isFocused) {
        onFocusChanged(isFocused)
    }
    
    return TextFieldInteraction(interactionSource, isFocused)
}

internal data class TextFieldInteraction(
    val interactionSource: MutableInteractionSource,
    val isFocused: Boolean
)

@Composable
internal fun TextFieldLayoutContainer(
    title: String?,
    isError: Boolean,
    supportingText: String?,
    textFieldInteraction: TextFieldInteraction,
    modifier: Modifier = Modifier,
    inputSlot: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        if (title != null) {
            androidx.compose.material3.Text(
                text = title,
                style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                color = androidx.compose.material3.MaterialTheme.colorScheme.extended.textSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        inputSlot()

        if (supportingText != null) {
            Spacer(modifier = Modifier.height(4.dp))
            androidx.compose.material3.Text(
                text = supportingText,
                color = if (isError) {
                    androidx.compose.material3.MaterialTheme.colorScheme.error
                } else {
                    androidx.compose.material3.MaterialTheme.colorScheme.extended.textTertiary
                },
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
internal fun getTextFieldModifier(
    isFocused: Boolean,
    isError: Boolean,
    enabled: Boolean
): Modifier {
    return Modifier
        .fillMaxWidth()
        .background(
            color = when {
                isFocused -> androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                enabled -> androidx.compose.material3.MaterialTheme.colorScheme.surface
                else -> androidx.compose.material3.MaterialTheme.colorScheme.extended.secondaryFill
            },
            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
        )
        .border(
            width = 1.dp,
            color = when {
                isError -> androidx.compose.material3.MaterialTheme.colorScheme.error
                isFocused -> androidx.compose.material3.MaterialTheme.colorScheme.primary
                else -> androidx.compose.material3.MaterialTheme.colorScheme.outline
            },
            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
        )
        .padding(12.dp)
}
