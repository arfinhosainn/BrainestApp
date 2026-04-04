package com.scelio.brainest.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brainest.feature.study.presentation.generated.resources.Res
import brainest.feature.study.presentation.generated.resources.ic_upoad
import brainest.feature.study.presentation.generated.resources.ic_voice
import brainest.feature.study.presentation.generated.resources.ic_wave
import com.scelio.brainest.designsystem.BrainestDraftEssay
import com.scelio.brainest.designsystem.BrainestError
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.Typography
import com.scelio.brainest.designsystem.components.buttons.BrainestButton
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadDocsBottomSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onUploadAudio: () -> Unit = {},
    onUploadDocument: () -> Unit = {},
    onRecordAudio: () -> Unit = {},
    ) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        tonalElevation = 0.dp,
        scrimColor = Color.Transparent,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(4.dp))

            BrainestButton(
                text = "Record Audio",
                onClick = onRecordAudio,
                modifier = modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(
                        imageVector = vectorResource(Res.drawable.ic_voice),
                        contentDescription = "",
                        modifier = modifier.size(20.dp),
                    )
                },
                textStyles = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = Typography.bodyMedium.fontFamily,
                ),
                backgroundColor = BrainestDraftEssay
            )
            BrainestButton(
                text = "Upload Audio",
                onClick = onUploadAudio,
                modifier = modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(
                        imageVector = vectorResource(Res.drawable.ic_upoad),
                        contentDescription = "",
                        modifier = modifier.size(20.dp),
                    )
                },

                textStyles = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = Typography.bodyMedium.fontFamily,
                ),
                backgroundColor = BrainestError
            )

            BrainestButton(
                text = "Upload Documents",
                onClick = onUploadDocument,
                modifier = modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(
                        imageVector = vectorResource(Res.drawable.ic_wave),
                        contentDescription = "",
                        modifier = modifier.size(20.dp),
                    )
                },
                textStyles = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = Typography.bodyMedium.fontFamily,
                ),
            )


        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewLoginSheet() {
    BrainestTheme {
        UploadDocsBottomSheet(onDismiss = {})

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDarkLoginSheet() {
    BrainestTheme(darkTheme = true) {
        UploadDocsBottomSheet(onDismiss = {})

    }
}
