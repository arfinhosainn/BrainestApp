package com.scelio.brainest.designsystem.components.textfields

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.extended
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BrainestTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    title: String? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    singleLine: Boolean = false,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    onFocusChanged: (Boolean) -> Unit = {},
) {
    val interaction = rememberTextFieldInteraction(onFocusChanged)
    val styleModifier = getTextFieldModifier(interaction.isFocused, isError, enabled)
    
    TextFieldLayoutContainer(
        title = title,
        isError = isError,
        supportingText = supportingText,
        textFieldInteraction = interaction,
        modifier = modifier
    ) {
        BasicTextField(
            state = state,
            enabled = enabled,
            lineLimits = if(singleLine) {
                TextFieldLineLimits.SingleLine
            } else TextFieldLineLimits.Default,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = if(enabled) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.extended.textPlaceholder
                }
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
            interactionSource = interaction.interactionSource,
            modifier = styleModifier
        )
    }
}

@Composable
private fun BrainestTextFieldDecoratorBox(
    state: TextFieldState,
    placeholder: String?,
    innerBox: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        if(state.text.isEmpty() && placeholder != null) {
            Text(
                text = placeholder,
                color = MaterialTheme.colorScheme.extended.textPlaceholder,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        innerBox()
    }
}


@Composable
@Preview(
    showBackground = true
)
fun BrainestTextFieldEmptyPreview() {
    BrainestTheme {
        BrainestTextField(
            state = rememberTextFieldState(),
            modifier = Modifier
                .width(300.dp),
            placeholder = "test@test.com",
            title = "Email",
            supportingText = "Please enter your email",
        )
    }
}

@Composable
@Preview(
    showBackground = true
)
fun BrainestTextFieldFilledPreview() {
    BrainestTheme {
        BrainestTextField(
            state = rememberTextFieldState(
                initialText = "test@test.com"
            ),
            modifier = Modifier
                .width(300.dp),
            placeholder = "test@test.com",
            title = "Email",
            supportingText = "Please enter your email",
        )
    }
}

@Composable
@Preview(
    showBackground = true
)
fun BrainestTextFieldDisabledPreview() {
    BrainestTheme {
        BrainestTextField(
            state = rememberTextFieldState(),
            modifier = Modifier
                .width(300.dp),
            placeholder = "test@test.com",
            title = "Email",
            supportingText = "Please enter your email",
            enabled = false
        )
    }
}

@Composable
@Preview(
    showBackground = true
)
fun BrainestTextFieldErrorPreview() {
    BrainestTheme(darkTheme = true) {
        BrainestTextField(
            state = rememberTextFieldState(),
            modifier = Modifier
                .width(300.dp),
            placeholder = "test@test.com",
            title = "Email",
            supportingText = "This is not a valid email",
            isError = true,
        )
    }
}