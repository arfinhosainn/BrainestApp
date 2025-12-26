package com.scelio.brainest.designsystem.components.chat.chat_math


import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import io.github.darriousliu.katex.core.MTMathView
import io.github.darriousliu.katex.core.MTMathViewMode
import io.github.darriousliu.katex.core.MTTextAlignment

@Composable
fun InlineMathText(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    mathColor: Color = color,
    mathFontSize: TextUnit = style.fontSize
) {
    val density = LocalDensity.current
    val tokens = remember(text) { tokenizeInlineMath(text) }

    if (tokens.none { it is InlineToken.Math }) {
        Text(
            text = text,
            modifier = modifier,
            style = style,
            color = color,
            softWrap = true,
            overflow = TextOverflow.Visible
        )
        return
    }

    val measureCache = remember(mathFontSize, density.density, density.fontScale) {
        mutableMapOf<String, IntSize>()
    }

    SubcomposeLayout { constraints ->
        val mathMeasures = mutableMapOf<Int, IntSize>()

        tokens.forEachIndexed { idx, token ->
            if (token is InlineToken.Math) {
                val cached = measureCache[token.latex]
                if (cached != null) {
                    mathMeasures[idx] = cached
                } else {
                    val placeables = subcompose("measure-math-$idx") {
                        MTMathView(
                            latex = token.latex,
                            fontSize = mathFontSize,
                            textColor = mathColor,
                            mode = MTMathViewMode.KMTMathViewModeText,
                            textAlignment = MTTextAlignment.KMTTextAlignmentCenter,
                            displayErrorInline = true,
                            errorFontSize = mathFontSize
                        )
                    }.map { measurable ->
                        measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
                    }
                    val w = placeables.maxOfOrNull { it.width } ?: 0
                    val h = placeables.maxOfOrNull { it.height } ?: 0
                    val size = IntSize(w, h)
                    mathMeasures[idx] = size
                    measureCache[token.latex] = size
                }
            }
        }

        val inlineContent = mutableMapOf<String, InlineTextContent>()
        val builder = AnnotatedString.Builder()

        tokens.forEachIndexed { idx, token ->
            when (token) {
                is InlineToken.Text -> builder.append(token.text)
                is InlineToken.Math -> {
                    val id = "math-$idx"
                    builder.appendInlineContent(id, "[math]")

                    val sizePx = mathMeasures[idx] ?: IntSize.Zero
                    val widthSp =
                        ((sizePx.width.toFloat()) / (density.density * density.fontScale)).sp
                    val heightSp =
                        ((sizePx.height.toFloat()) / (density.density * density.fontScale)).sp

                    inlineContent[id] = InlineTextContent(
                        placeholder = Placeholder(
                            width = widthSp,
                            height = heightSp,
                            placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                        )
                    ) {
                        MTMathView(
                            latex = token.latex,
                            fontSize = mathFontSize,
                            textColor = mathColor,
                            mode = MTMathViewMode.KMTMathViewModeText,
                            textAlignment = MTTextAlignment.KMTTextAlignmentCenter,
                            displayErrorInline = true,
                            errorFontSize = mathFontSize
                        )
                    }
                }
            }
        }

        val textPlaceables = subcompose("final-text") {
            Text(
                text = builder.toAnnotatedString(),
                style = style,
                modifier = modifier,
                color = color,
                inlineContent = inlineContent,
                softWrap = true,
                overflow = TextOverflow.Visible
            )
        }.map { measurable ->
            measurable.measure(constraints)
        }

        val width = textPlaceables.maxOfOrNull { it.width } ?: 0
        val height = textPlaceables.maxOfOrNull { it.height } ?: 0

        layout(width, height) {
            textPlaceables.forEach { it.place(0, 0) }
        }
    }
}