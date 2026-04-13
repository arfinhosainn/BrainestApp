package com.scelio.brainest.domain.chat

/**
 * Generates a fallback chat title from the user's first message when AI title generation fails.
 *
 * This extracts the topic from the message by:
 * 1. Taking the first non-blank line
 * 2. Removing markdown formatting
 * 3. Stripping common lead-in phrases ("Can you", "Explain", etc.)
 * 4. Taking the first 7 topic words
 * 5. Title-casing and truncating to 48 characters
 *
 * @param messageContent The user's first message
 * @return A generated title, or "Untitled chat" if no topic could be extracted
 */
fun generateFallbackChatTitle(messageContent: String): String {
    val firstLine = messageContent
        .lineSequence()
        .firstOrNull { it.isNotBlank() }
        .orEmpty()
        .replace(Regex("""[`*_#>\[\]\(\)]"""), " ")
        .replace(Regex("""\s+"""), " ")
        .trim()

    if (firstLine.isBlank()) return "Untitled chat"

    val withoutLeadIn = firstLine
        .replace(
            Regex(
                pattern = """^(can you|could you|would you|please|help me|i need help with|explain|solve|show me|tell me about|what is|how do i|how to)\s+""",
                option = RegexOption.IGNORE_CASE
            ),
            ""
        )
        .trim()
        .ifBlank { firstLine }

    val topicWords = withoutLeadIn
        .split(Regex("""\s+"""))
        .filter { it.isNotBlank() }
        .take(7)
        .mapIndexed { index, word ->
            val normalized = word.trim { it.isWhitespace() || it == ',' || it == '.' || it == '?' || it == '!' || it == ':' || it == ';' }
            if (normalized.isEmpty()) {
                ""
            } else if (index == 0) {
                normalized.replaceFirstChar { char -> char.titlecase() }
            } else {
                normalized.lowercase()
            }
        }
        .filter { it.isNotBlank() }

    val title = topicWords.joinToString(" ").trim()
    if (title.isBlank()) return "Untitled chat"

    return if (title.length > 48) {
        title.take(45).trimEnd() + "..."
    } else {
        title
    }
}

/**
 * Normalizes a generated title by cleaning formatting and truncating.
 *
 * - Takes the first non-blank line
 * - Removes quotes, punctuation at end
 * - Collapses whitespace
 * - Truncates to 48 characters with ellipsis if needed
 *
 * @param title The raw generated title
 * @return The normalized title, or empty string if invalid
 */
fun normalizeGeneratedTitle(title: String): String {
    val normalized = title
        .lineSequence()
        .firstOrNull { it.isNotBlank() }
        .orEmpty()
        .replace(Regex("""["'`]+"""), "")
        .replace(Regex("""[.!?,:;]+$"""), "")
        .replace(Regex("""\s+"""), " ")
        .trim()

    if (normalized.isBlank()) return ""

    return if (normalized.length > 48) {
        normalized.take(45).trimEnd() + "..."
    } else {
        normalized
    }
}
