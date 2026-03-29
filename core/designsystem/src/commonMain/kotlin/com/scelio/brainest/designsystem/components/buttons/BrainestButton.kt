package com.scelio.brainest.designsystem.components.buttons


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.extended
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BrainestButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    disabledBackgroundColor: Color = MaterialTheme.colorScheme.extended.disabledFill,
    disabledContentColor: Color = MaterialTheme.colorScheme.extended.textDisabled,
    shape: Shape = RoundedCornerShape(40.dp),
    border: BorderStroke? = null,
    textStyles: TextStyle = TextStyle(),
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    loadingIndicatorColor: Color = contentColor
) {
    val colors = ButtonDefaults.buttonColors(
        containerColor = backgroundColor,
        contentColor = contentColor,
        disabledContainerColor = disabledBackgroundColor,
        disabledContentColor = disabledContentColor
    )
    val resolvedTextStyle = textStyles.merge(
        TextStyle(
            fontSize = fontSize,
            fontWeight = fontWeight,
            fontFamily = fontFamily
        )
    )

    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        border = border
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(10.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(15.dp)
                    .alpha(
                        alpha = if (isLoading) 1f else 0f
                    ),
                strokeWidth = 1.5.dp,
                color = loadingIndicatorColor
            )
            Box(
                modifier = Modifier
                    .alpha(if (isLoading) 0f else 1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Leading icon (positioned at start)
                if (leadingIcon != null) {
                    Box(
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        leadingIcon()
                    }
                }
                // Text (centered)
                Text(
                    text = text,
                    style = resolvedTextStyle
                )
                // Trailing icon (positioned at end)
                if (trailingIcon != null) {
                    Box(
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        trailingIcon()
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun BrainestPrimaryButtonPreview() {
    BrainestTheme(
        darkTheme = false
    ) {
        BrainestButton(
            text = "Hello world!",
            onClick = {},
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null
                )
            }

        )
    }
}

@Composable
@Preview
fun BrainestSecondaryButtonPreview() {
    BrainestTheme(
        darkTheme = true
    ) {
        BrainestButton(
            text = "Hello world!",
            onClick = {},
            backgroundColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.extended.textSecondary,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.extended.disabledOutline)
        )
    }
}

@Composable
@Preview
fun BrainestDestructivePrimaryButtonPreview() {
    BrainestTheme(
        darkTheme = true
    ) {
        BrainestButton(
            text = "Hello world!",
            onClick = {},
            backgroundColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError
        )
    }
}

@Composable
@Preview
fun BrainestDestructiveSecondaryButtonPreview() {
    BrainestTheme(
        darkTheme = true
    ) {
        BrainestButton(
            text = "Hello world!",
            onClick = {},
            backgroundColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.error,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
        )
    }
}

@Composable
@Preview
fun BrainestTextButtonPreview() {
    BrainestTheme(
        darkTheme = true
    ) {
        BrainestButton(
            text = "Hello world!",
            onClick = {},
            backgroundColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.tertiary
        )
    }
}
