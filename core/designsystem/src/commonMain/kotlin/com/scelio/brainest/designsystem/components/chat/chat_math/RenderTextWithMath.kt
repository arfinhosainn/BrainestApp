package com.scelio.brainest.designsystem.components.chat.chat_math


import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.darriousliu.katex.core.MTMathView
import io.github.darriousliu.katex.core.MTMathViewMode
import io.github.darriousliu.katex.core.MTTextAlignment

@Composable
fun RenderTextWithInlineMath(
    text: String,
    isUser: Boolean,
    backgroundColor: Color
) {
    val processed = remember(text) { preprocessMathContent(text) }
    val baseStyle = MaterialTheme.typography.bodyLarge.copy(
        fontSize = if (isUser) 16.sp else 18.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
        fontFamily = if (isUser) FontFamily.Default else FontFamily.Serif
    )

    val textColor = if (isUser) Color.Black else MaterialTheme.colorScheme.onSurface
    val mathColor = textColor

    val pieces = remember(processed) { splitDisplayMathPieces(processed) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        pieces.forEach { piece ->
            when (piece) {
                is DisplayPiece.Text -> {
                    if (piece.text.isNotEmpty()) {
                        InlineMathText(
                            text = piece.text,
                            style = baseStyle,
                            color = textColor,
                            mathColor = mathColor,
                            mathFontSize = 18.sp,
                        )
                    }
                }

                is DisplayPiece.DisplayMath -> {
                    val clipboardManager = LocalClipboard.current

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    backgroundColor
                                )
                                .padding(12.dp)
                                .horizontalScroll(rememberScrollState()),
                            contentAlignment = Alignment.Center
                        ) {
                            MTMathView(
                                latex = piece.latex,
                                modifier = Modifier.fillMaxWidth(),
                                fontSize = if (isUser) 18.sp else 20.sp,
                                textColor = mathColor,
                                mode = MTMathViewMode.KMTMathViewModeDisplay,
                                textAlignment = MTTextAlignment.KMTTextAlignmentCenter,
                                displayErrorInline = true,
                                errorFontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}