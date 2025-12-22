package com.scelio.brainest.presentation.chat_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import brainest.feature.chat.presentation.generated.resources.Res
import brainest.feature.chat.presentation.generated.resources.ic_camera
import brainest.feature.chat.presentation.generated.resources.ic_document
import brainest.feature.chat.presentation.generated.resources.ic_gallery
import brainest.feature.chat.presentation.generated.resources.ic_mic
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AttachmentFabMenu(
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    onDocumentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.size(40.dp).background(Color.Transparent)) {
        FloatingActionButton(
            onClick = { expanded = !expanded },
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            containerColor = Color(0xFF787880).copy(alpha = 0.30f),
            contentColor = MaterialTheme.colorScheme.onBackground,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
            shape = CircleShape,
        ) {
            Icon(
                imageVector = if (expanded) Icons.Default.Close else Icons.Default.Add,
                contentDescription = "Open attachment menu",
                modifier = Modifier.size(20.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = Color.Transparent,
            shadowElevation = 0.dp,
            offset = DpOffset((-10).dp, 0.dp)

        ) {
            DropdownMenuItem(
                text = { Text("Take Photo") },
                onClick = {
                    expanded = false
                    onGalleryClick()
                },
                leadingIcon = {
                    IconButton(
                        onClick = {},
                        colors = IconButtonColors(
                            containerColor = Color(0xFF787880).copy(alpha = 0.30f),
                            contentColor = Color.Black,
                            disabledContainerColor = Color(0xFFEAE6E2),
                            disabledContentColor = Color.Black
                        )
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_camera),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            )

            Spacer(Modifier.height(15.dp))

            DropdownMenuItem(
                text = { Text("Upload Photo") },
                onClick = {
                    expanded = false
                    onCameraClick()
                },
                leadingIcon = {
                    IconButton(
                        onClick = {},
                        colors = IconButtonColors(
                            containerColor = Color(0xFF787880).copy(alpha = 0.30f),
                            contentColor = Color.Black,
                            disabledContainerColor = Color(0xFFEAE6E2),
                            disabledContentColor = Color.Black
                        )
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_gallery),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            )
            Spacer(Modifier.height(15.dp))

            DropdownMenuItem(
                text = { Text("Upload File") },
                onClick = {
                    expanded = false
                    onDocumentClick()
                },
                leadingIcon = {
                    IconButton(
                        onClick = {},
                        colors = IconButtonColors(
                            containerColor = Color(0xFF787880).copy(alpha = 0.30f),
                            contentColor = Color.Black,
                            disabledContainerColor = Color(0xFFEAE6E2),
                            disabledContentColor = Color.Black
                        )
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_document),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun PreviewAttachmentFabMenu() {
    AttachmentFabMenu(
        onGalleryClick = {},
        onCameraClick = {},
        onDocumentClick = {}
    )
}