package com.scelio.brainest.designsystem.components.chat.chat_math

sealed class ContentSegment {
    data class HeadingSegment(val text: String, val level: Int) : ContentSegment()
    data class ListItemSegment(val text: String, val type: ListType, val number: Int? = null) :
        ContentSegment()

    data class DisplayMathSegment(val latex: String) : ContentSegment()
    data class TextWithInlineMathSegment(val originalText: String) : ContentSegment()
    data object SpacerSegment : ContentSegment()
    data class CodeBlockSegment(val code: String) : ContentSegment()
    data class TableSegment(val markdownTable: String) : ContentSegment() //

}

enum class ListType { BULLET, NUMBERED, ROMAN }