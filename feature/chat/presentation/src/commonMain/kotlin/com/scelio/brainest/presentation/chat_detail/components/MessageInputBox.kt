package com.scelio.brainest.presentation.chat_detail.components


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.domain.chat.ImageEncoder
import com.scelio.brainest.presentation.chat_detail.UploadedDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.ui.tooling.preview.Preview

private val LightCreameRed = Color(0xFFFFF5F5)

@Composable
fun MessageInputBox(
    textFieldState: TextFieldState,
    selectedImages: List<ByteArray>,
    selectedDocument: UploadedDocument?,
    enabled: Boolean,
    onSendMessage: () -> Unit,
    onImageSelected: (List<ByteArray>) -> Unit,
    onImageRemoved: (Int) -> Unit,
    onDocumentSelected: (UploadedDocument) -> Unit,
    onDocumentCleared: () -> Unit,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    onDocumentClick: () -> Unit,
    onMicClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var imageBase64List by remember { mutableStateOf<List<String>>(emptyList()) }
    LaunchedEffect(selectedImages) {
        imageBase64List = selectedImages.map { bytes ->
            withContext(Dispatchers.Default) {
                ImageEncoder.encodeToBase64(bytes, "image/jpeg")
            }
        }
    }

    val animatedBackgroundColor by animateColorAsState(
        targetValue = LightCreameRed,
        animationSpec = tween(300),
        label = "backgroundColor"
    )


    Column(modifier = Modifier.padding(horizontal = 12.022.dp, vertical = 10.dp)) {
        // Document preview
        if (selectedDocument != null) {
            DocumentPreview(
                document = selectedDocument,
                onClear = onDocumentCleared,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }

        if (selectedImages.isNotEmpty()) {
            ImagesPreview(
                images = selectedImages,
                imageBase64List = imageBase64List,
                onImageRemoved = onImageRemoved,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AttachmentFabMenu(
                onGalleryClick = onGalleryClick,
                onCameraClick = onCameraClick,
                onDocumentClick = onDocumentClick
            )

            BasicTextField(
                state = textFieldState,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                enabled = enabled,
                textStyle = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Send
                ),
                decorator = { innerTextField ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (textFieldState.text.isEmpty()) {
                                Text(
                                    text = getPlaceholderText(
                                        selectedImages.size,
                                        selectedDocument
                                    ),
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                            innerTextField()
                        }

                        TrailingButton(
                            hasContent = textFieldState.text.isNotEmpty() ||
                                    selectedImages.isNotEmpty() ||
                                    selectedDocument != null,
                            enabled = enabled,
                            onSendClick = onSendMessage,
                            onMicClick = onMicClick
                        )
                    }
                }
            )
        }
    }
}


private fun getPlaceholderText(imageCount: Int, document: UploadedDocument?): String {
    return when {
        document != null -> "Add a message about this document"
        imageCount > 1 -> "Add a caption ($imageCount images)"
        imageCount == 1 -> "Add a caption (1 image)"
        else -> "Ask a Follow-Up"
    }
}


@Preview(showBackground = true, name = "With Text Typed")
@Composable
private fun MessageInputBoxPreview_WithText() {
    val textState = remember { TextFieldState("Here's my follow-up question...") }

    Column {

        BrainestTheme(darkTheme = false) {
            MessageInputBox(
                textFieldState = textState,
                selectedImages = emptyList(),
                selectedDocument = null,
                enabled = true,
                onSendMessage = {},
                onImageSelected = {},
                onImageRemoved = {},
                onDocumentSelected = {},
                onDocumentCleared = {},
                onGalleryClick = {},
                onCameraClick = {},
                onDocumentClick = {},
                onMicClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

        }
        BrainestTheme(darkTheme = true) {
            MessageInputBox(
                textFieldState = textState,
                selectedImages = emptyList(),
                selectedDocument = null,
                enabled = true,
                onSendMessage = {},
                onImageSelected = {},
                onImageRemoved = {},
                onDocumentSelected = {},
                onDocumentCleared = {},
                onGalleryClick = {},
                onCameraClick = {},
                onDocumentClick = {},
                onMicClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}