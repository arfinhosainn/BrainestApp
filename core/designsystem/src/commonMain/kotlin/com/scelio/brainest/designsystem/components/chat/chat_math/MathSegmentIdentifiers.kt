package com.scelio.brainest.designsystem.components.chat.chat_math


/**
 * Detect if a line is potentially part of a markdown table,
 * either a header, separator, or data row.
 */
fun isTableLine(line: String): Boolean {
    val trimmed = line.trim()

    // Must contain at least one pipe
    if (!trimmed.contains("|")) return false

    // Check if it's a separator line (| --- | --- |)
    if (trimmed.matches(Regex("""^[\s|:\-]+$"""))) {
        return true
    }

    // Check if it's a regular table row with >= 2 columns
    val pipeCount = trimmed.count { it == '|' }
    return pipeCount >= 1
}

/**
 * Detect if a line is a table separator row
 * e.g. |---|---|, | :--- | :---: | ---: |
 */
fun isTableSeparator(line: String): Boolean {
    val trimmed = line.trim()
    return trimmed.matches(Regex("""^\|?[\s:\-]+\|[\s:\-|]+\|?$"""))
}

/**
 * Extracts a table block (header + separator + rows) starting from `startIndex`.
 * Returns [tableMarkdown, lastIndexOfTable].
 */
fun extractTable(lines: List<String>, startIndex: Int): Pair<String, Int> {
    if (startIndex >= lines.size) return "" to startIndex

    val tableLines = mutableListOf<String>()
    var i = startIndex
    var foundSeparator = false

    // Not a table start
    if (!isTableLine(lines[i])) {
        return "" to startIndex
    }

    // Collect consecutive table lines
    while (i < lines.size) {
        val line = lines[i]

        if (!isTableLine(line)) {
            // If we haven't found a separator, not a real table
            if (!foundSeparator) return "" to startIndex
            break
        }

        tableLines.add(line)

        if (!foundSeparator && isTableSeparator(line)) {
            foundSeparator = true
        }

        i++
    }

    // Valid table must have >= 2 lines and a separator
    if (tableLines.size < 2 || !foundSeparator) {
        return "" to startIndex
    }

    // Ensure separator line is after header
    if (!isTableSeparator(tableLines[1]) && !isTableSeparator(tableLines[0])) {
        return "" to startIndex
    }

    return tableLines.joinToString("\n") to (i - 1)
}



fun intToRoman(num: Int): String {
    val values = intArrayOf(1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1)
    val literals = arrayOf("M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I")
    val result = StringBuilder()
    var number = num
    for (i in values.indices) {
        while (number >= values[i]) {
            result.append(literals[i])
            number -= values[i]
        }
    }
    return result.toString().lowercase()
}

/**
 * Preprocess external math markers into $...$ and $$...$$ and clean common quirks.
 */
fun preprocessMathContent(content: String): String {
    var processed = content.replace("\r\n", "\n").replace("\r", "\n")

    // 1) KATEX tokens -> $...$ / $$...$$
    processed = processed.replace(
        Regex("""KATEX_INLINE_OPEN\s*([\s\S]+?)\s*KATEX_INLINE_CLOSE""")
    ) { m -> "${'$'}${sanitizeLatexInsideDelimiters(m.groupValues[1])}${'$'}" }

    processed = processed.replace(
        Regex("""KATEX_DISPLAY_OPEN\s*([\s\S]+?)\s*KATEX_DISPLAY_CLOSE""")
    ) { m -> "${'$'}${'$'}${sanitizeLatexInsideDelimiters(m.groupValues[1])}${'$'}${'$'}" }

    // 2) Fenced math blocks ```math / ```latex
    processed = processed.replace(
        Regex("""```(?:math|latex)\s+([\s\S]+?)```""", RegexOption.IGNORE_CASE)
    ) { m -> "${'$'}${'$'}${sanitizeLatexInsideDelimiters(m.groupValues[1])}${'$'}${'$'}" }

    // 3) Escaped variants
    processed = processed.replace(
        Regex("""\\KATEX_INLINE_OPEN([\s\S]+?)\\KATEX_INLINE_CLOSE""")
    ) { m -> "${'$'}${sanitizeLatexInsideDelimiters(m.groupValues[1])}${'$'}" }

    processed = processed.replace(
        Regex("""\\```math
([\s\S]+?)\\```""")
    ) { m -> "${'$'}${'$'}${sanitizeLatexInsideDelimiters(m.groupValues[1])}${'$'}${'$'}" }

    processed = processed.replace(
        Regex("""\\\\KATEX_INLINE_OPEN([\s\S]+?)\\\\KATEX_INLINE_CLOSE""")
    ) { m -> "${'$'}${sanitizeLatexInsideDelimiters(m.groupValues[1])}${'$'}" }

    processed = processed.replace(
        Regex("""\\\\```math
([\s\S]+?)\\\\```""")
    ) { m -> "${'$'}${'$'}${sanitizeLatexInsideDelimiters(m.groupValues[1])}${'$'}${'$'}" }

    return processed
}

// Fix common quirks inside math content only
fun sanitizeLatexInsideDelimiters(latex: String): String {
    var s = latex.trim()
    // 1. Turn "\ %67" into "\%67"
    s = s.replace(Regex("""\\\s+%"""), """\%""")

    // 2. Escape actual bare % characters so they don't act as comments
    // Only replace '%' if it's not already escaped by '\'
    s = s.replace(Regex("""(?<!\\)%"""), """\%""")

    return s
}

/**
 * Parse rich text into structured segments, including DisplayMathSegment for $$...$$.
 */
fun parseContentSegments(content: String): List<ContentSegment> {
    val segments = mutableListOf<ContentSegment>()
    val preprocessedContent = preprocessMathContent(content)
    val lines = preprocessedContent.split('\n')

    var currentListNumber = 1
    var isInNumberedList = false
    var previousLineWasEmpty = false

    var inCodeBlock = false
    val codeBuffer = StringBuilder()

    var i = 0
    while (i < lines.size) {
        val line = lines[i]
        val trimmedLine = line.trim()

        // Fenced code blocks
        if (trimmedLine.startsWith("```")) {
            if (!inCodeBlock) {
                inCodeBlock = true
                codeBuffer.clear()
            } else {
                inCodeBlock = false
                segments.add(ContentSegment.CodeBlockSegment(codeBuffer.toString().trimEnd()))
            }
            previousLineWasEmpty = false
            i++
            continue
        }
        if (inCodeBlock) {
            codeBuffer.appendLine(line) // keep indentation
            i++
            continue
        }

        // 👉 CHECK FOR TABLES HERE
        if (!inCodeBlock && isTableLine(trimmedLine)) {
            val nextLine = lines.getOrNull(i + 1)?.trim() ?: ""

            // Only treat as a table if there's a separator
            if (isTableSeparator(trimmedLine) || isTableSeparator(nextLine)) {
                val (tableContent, endIndex) = extractTable(lines, i)
                if (tableContent.isNotEmpty()) {
                    segments.add(ContentSegment.TableSegment(tableContent))
                    i = endIndex + 1
                    previousLineWasEmpty = false
                    isInNumberedList = false
                    currentListNumber = 1
                    continue
                }
            }
        }

        // Normal parsing logic
        when {
            trimmedLine.isEmpty() -> {
                if (!previousLineWasEmpty && segments.isNotEmpty()) {
                    segments.add(ContentSegment.SpacerSegment)
                }
                isInNumberedList = false
                currentListNumber = 1
                previousLineWasEmpty = true
            }

            trimmedLine.startsWith("#") && trimmedLine.contains(" ") -> {
                val level = trimmedLine.takeWhile { it == '#' }.length
                val headingText = trimmedLine.drop(level).trim()
                if (headingText.isNotEmpty()) {
                    segments.add(ContentSegment.HeadingSegment(headingText, level))
                }
                isInNumberedList = false
                currentListNumber = 1
                previousLineWasEmpty = false
            }

            trimmedLine.startsWith("• ") || trimmedLine.startsWith("- ") || trimmedLine.startsWith("* ") -> {
                val bulletText = trimmedLine.drop(2).trim()
                segments.add(ContentSegment.ListItemSegment(bulletText, ListType.BULLET))
                isInNumberedList = false
                currentListNumber = 1
                previousLineWasEmpty = false
            }

            trimmedLine.matches(Regex("""^\d+\.\s+.*""")) -> {
                val extractedNumber = trimmedLine.takeWhile { it.isDigit() }.toIntOrNull() ?: 1
                if (!isInNumberedList || extractedNumber == 1) {
                    currentListNumber = extractedNumber
                    isInNumberedList = true
                } else {
                    currentListNumber++
                }
                val numberedText = trimmedLine.substring(trimmedLine.indexOf('.') + 1).trim()
                segments.add(ContentSegment.ListItemSegment(numberedText, ListType.NUMBERED, currentListNumber))
                previousLineWasEmpty = false
            }

            trimmedLine.matches(Regex("""^[ivxlcdm]+\.\s+.*""", RegexOption.IGNORE_CASE)) -> {
                val romanText = trimmedLine.substring(trimmedLine.indexOf('.') + 1).trim()
                segments.add(ContentSegment.ListItemSegment(romanText, ListType.ROMAN))
                isInNumberedList = false
                currentListNumber = 1
                previousLineWasEmpty = false
            }

            else -> {
                // Split text + possible inline/display math
                val pieces = splitDisplayMathPieces(line)
                pieces.forEach { p ->
                    when (p) {
                        is DisplayPiece.Text -> if (p.text.isNotBlank())
                            segments.add(ContentSegment.TextWithInlineMathSegment(p.text))
                        is DisplayPiece.DisplayMath -> if (p.latex.isNotBlank())
                            segments.add(ContentSegment.DisplayMathSegment(p.latex))
                    }
                }
                isInNumberedList = false
                currentListNumber = 1
                previousLineWasEmpty = false
            }
        }
        i++
    }

    return segments.filter {
        when (it) {
            is ContentSegment.TextWithInlineMathSegment -> it.originalText.isNotBlank()
            is ContentSegment.ListItemSegment -> it.text.isNotBlank()
            is ContentSegment.DisplayMathSegment -> it.latex.isNotBlank()
            is ContentSegment.TableSegment -> it.markdownTable.isNotBlank()
            else -> true
        }
    }
}

// Pieces used for splitting $$...$$ blocks before inline parsing
sealed interface DisplayPiece {
    data class Text(val text: String) : DisplayPiece
    data class DisplayMath(val latex: String) : DisplayPiece
}

/**
 * Split into $$...$$ display math vs plain text pieces.
 * Escaped \$\$ (i.e., "\$\$") becomes a literal "$$".
 */
fun splitDisplayMathPieces(input: String): List<DisplayPiece> {
    val out = mutableListOf<DisplayPiece>()
    val sb = StringBuilder()
    var i = 0
    var inDisplay = false

    while (i < input.length) {
        // Handle escaped "$$"
        if (input[i] == '\\' && i + 2 < input.length && input[i + 1] == '$' && input[i + 2] == '$') {
            sb.append("$$")
            i += 3
            continue
        }

        // Detect "$$"
        if (i + 1 < input.length && input[i] == '$' && input[i + 1] == '$') {
            if (inDisplay) {
                // closing
                out.add(DisplayPiece.DisplayMath(sb.toString()))
                sb.clear()
                inDisplay = false
            } else {
                // opening
                if (sb.isNotEmpty()) {
                    out.add(DisplayPiece.Text(sb.toString()))
                    sb.clear()
                }
                inDisplay = true
            }
            i += 2
            continue
        }

        sb.append(input[i])
        i += 1
    }

    if (sb.isNotEmpty()) {
        if (inDisplay) {
            // unmatched $$ -> treat as text
            out.add(DisplayPiece.Text("$$${sb}"))
        } else {
            out.add(DisplayPiece.Text(sb.toString()))
        }
    }
    return out
}


sealed interface InlineToken {
    data class Text(val text: String) : InlineToken
    data class Math(val latex: String) : InlineToken
}

/**
 * Tokenize inline $...$ while preserving all other characters verbatim.
 * Escaped \$ is treated as literal.
 * We do NOT handle $$...$$ here; that should be split beforehand.
 */
fun tokenizeInlineMath(text: String): List<InlineToken> {
    val out = mutableListOf<InlineToken>()
    val sb = StringBuilder()
    var i = 0
    var inMath = false

    while (i < text.length) {
        val c = text[i]

        // Escaped \$ -> literal $
        if (c == '\\' && i + 1 < text.length && text[i + 1] == '$') {
            sb.append('$')
            i += 2
            continue
        }

        // Don't consume "$$" here (display math handled elsewhere)
        if (c == '$' && i + 1 < text.length && text[i + 1] == '$') {
            sb.append(c)
            i += 1
            continue
        }

        if (c == '$') {
            if (inMath) {
                // close
                out.add(InlineToken.Math(sb.toString()))
                sb.clear()
                inMath = false
            } else {
                // open: flush preceding text
                if (sb.isNotEmpty()) {
                    out.add(InlineToken.Text(sb.toString()))
                    sb.clear()
                }
                inMath = true
            }
            i += 1
            continue
        }

        sb.append(c)
        i += 1
    }

    if (sb.isNotEmpty()) {
        if (inMath) {
            // unmatched $, treat as text fragment with a leading $
            out.add(InlineToken.Text("$${sb}"))
        } else {
            out.add(InlineToken.Text(sb.toString()))
        }
    }

    return out
}