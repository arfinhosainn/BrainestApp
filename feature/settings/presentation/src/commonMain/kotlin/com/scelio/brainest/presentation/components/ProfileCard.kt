package com.scelio.brainest.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import kotlinx.coroutines.delay
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brainest.feature.settings.presentation.generated.resources.Res
import brainest.feature.settings.presentation.generated.resources.ic_pen
import brainest.feature.settings.presentation.generated.resources.ic_checkmark
import brainest.feature.settings.presentation.generated.resources.settings_edit_name
import brainest.feature.settings.presentation.generated.resources.settings_save_name
import brainest.feature.settings.presentation.generated.resources.settings_your_name
import com.scelio.brainest.designsystem.BrainestPrimary
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.components.navbar.home.CircularProfilePicture
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProfileCard(
    name: String,
    joinedText: String,
    modifier: Modifier = Modifier,
    onNameChange: (String) -> Unit = {}
) {
    var isEditing by rememberSaveable { mutableStateOf(false) }
    var draftName by rememberSaveable(name) { mutableStateOf(name) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var hasFocused by rememberSaveable { mutableStateOf(false) }

    fun commitName() {
        val updatedName = draftName.trim()
        if (updatedName.isNotEmpty() && updatedName != name) {
            onNameChange(updatedName)
        } else if (updatedName.isEmpty()) {
            draftName = name
        }

        isEditing = false
        hasFocused = false
        keyboardController?.hide()
    }

    LaunchedEffect(name) {
        if (!isEditing) {
            draftName = name
        }
    }

    LaunchedEffect(isEditing) {
        if (isEditing) {
            hasFocused = false
            delay(50) // Small delay to ensure composition is complete
            focusRequester.requestFocus()
        }
    }

    Surface(
        color = Color(0xFF1AA662),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .defaultMinSize(minHeight = 130.dp)
                .padding(horizontal = 20.dp, vertical = 22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(86.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.graphicsLayer(
                        scaleX = 1.35f,
                        scaleY = 1.35f
                    )
                ) {
                    CircularProfilePicture()
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (isEditing) {
                    BasicTextField(
                        value = draftName,
                        onValueChange = { draftName = it },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        ),
                        cursorBrush = SolidColor(Color.White),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { commitName() }),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .onFocusChanged { focusState ->
                                if (hasFocused && !focusState.isFocused) {
                                    commitName()
                                }
                                if (focusState.isFocused) {
                                    hasFocused = true
                                }
                            },
                        decorationBox = { innerTextField ->
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(contentAlignment = Alignment.CenterStart) {
                                    if (draftName.isBlank()) {
                                        Text(
                                            text = stringResource(Res.string.settings_your_name),
                                            style = MaterialTheme.typography.headlineMedium.copy(
                                                color = Color.White.copy(alpha = 0.65f),
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                    }
                                    innerTextField()
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.72f)
                                        .height(2.dp)
                                        .background(Color.White.copy(alpha = 0.7f))
                                )
                            }
                        }
                    )
                } else {
                    Text(
                        text = name,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        fontSize = 24.sp

                    )
                }

                Text(
                    text = joinedText,
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(
                onClick = {
                    if (isEditing) {
                        commitName()
                    } else {
                        draftName = name
                        isEditing = true
                    }
                },
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.22f),
                        shape = CircleShape
                    ),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    painter = painterResource(if (isEditing) Res.drawable.ic_checkmark else Res.drawable.ic_pen),
                    contentDescription = if (isEditing) {
                        stringResource(Res.string.settings_save_name)
                    } else {
                        stringResource(Res.string.settings_edit_name)
                    },
                    modifier = Modifier.size(25.dp),
                    tint = Color.White
                )
            }
        }
    }
}

@Preview
@Composable
private fun ProfileCardPreview() {
    BrainestTheme {
        var name by rememberSaveable { mutableStateOf("Wdz") }

        Column(modifier = Modifier.fillMaxSize()){

            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.padding(16.dp)
            ) {
                ProfileCard(
                    name = name,
                    joinedText = "Joined Oct 2017",
                    onNameChange = { name = it }
                )
            }
        }

    }
}
