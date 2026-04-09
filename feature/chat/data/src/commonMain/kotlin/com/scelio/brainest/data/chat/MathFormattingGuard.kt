package com.scelio.brainest.data.chat

internal object MathFormattingGuard {
    private val inlineLatexDelimiterRegex = Regex("""\\\(\s*([\s\S]+?)\s*\\\)""")
    private val displayLatexDelimiterRegex = Regex("""\\\[\s*([\s\S]+?)\s*\\\]""")
    private val codeFenceRegex = Regex("""```[\s\S]+?```""")
    private val svgRegex = Regex("""<svg[\s\S]*?</svg>""", RegexOption.IGNORE_CASE)
    private val displayMathRegex = Regex("""\$\$[\s\S]+?\$\$""")
    private val inlineMathRegex = Regex("""(?<!\\)\$(?!\$)[\s\S]+?(?<!\\)\$(?!\$)""")
    private val bareEquationRegex = Regex(
        pattern = """(?m)(^|[^\w\\$])([A-Za-z0-9][A-Za-z0-9_^{}/().+-]*\s*=\s*[A-Za-z0-9\\{(][^\n]*)"""
    )

    fun normalize(text: String): String {
        return text
            .replace("\r\n", "\n")
            .replace("\r", "\n")
            .replace(inlineLatexDelimiterRegex) { match ->
                "${'$'}${match.groupValues[1].trim()}${'$'}"
            }
            .replace(displayLatexDelimiterRegex) { match ->
                "${'$'}${'$'}${match.groupValues[1].trim()}${'$'}${'$'}"
            }
            .trim()
    }

    fun needsRepair(text: String): Boolean {
        val normalized = normalize(text)
        if (normalized.isBlank()) return false

        val plainText = normalized
            .replace(codeFenceRegex, " ")
            .replace(svgRegex, " ")
            .replace(displayMathRegex, " ")
            .replace(inlineMathRegex, " ")

        return bareEquationRegex.containsMatchIn(plainText)
    }
}
