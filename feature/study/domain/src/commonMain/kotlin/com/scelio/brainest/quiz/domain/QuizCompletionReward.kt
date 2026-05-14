package com.scelio.brainest.quiz.domain

data class QuizCompletionReward(
    val earnedExp: Int,
    val earnedDiamonds: Int
)

data class QuizRewardSeed(
    val deckId: String,
    val completedAtEpochMillis: Long,
    val totalQuestions: Int,
    val answeredQuestions: Int,
    val correctAnswers: Int
)

private val expRewardOptions = intArrayOf(25, 50, 80)
private val diamondRewardOptions = intArrayOf(1, 2, 5)
private const val maxRewardedQuizCompletionsPerDay = 5
private const val millisPerDay = 86_400_000L

fun deriveQuizCompletionReward(
    deckId: String,
    completedAtEpochMillis: Long,
    totalQuestions: Int,
    answeredQuestions: Int,
    correctAnswers: Int
): QuizCompletionReward {
    val seed = buildString {
        append(deckId)
        append('|')
        append(completedAtEpochMillis)
        append('|')
        append(totalQuestions)
        append('|')
        append(answeredQuestions)
        append('|')
        append(correctAnswers)
    }

    val hash = stableFnv1a(seed)
    val expIndex = (hash % expRewardOptions.size.toUInt()).toInt()
    val diamondIndex = ((hash / expRewardOptions.size.toUInt()) % diamondRewardOptions.size.toUInt()).toInt()

    return QuizCompletionReward(
        earnedExp = expRewardOptions[expIndex],
        earnedDiamonds = diamondRewardOptions[diamondIndex]
    )
}

fun computeEffectiveQuizRewards(
    completions: List<QuizRewardSeed>
): List<QuizCompletionReward> {
    if (completions.isEmpty()) return emptyList()

    val indexedCompletions = completions.withIndex()
    val rewardsByIndex = mutableMapOf<Int, QuizCompletionReward>()

    val groupedByDay: Map<Long, List<IndexedValue<QuizRewardSeed>>> = indexedCompletions.groupBy {
        epochMillisToUtcDayKey(it.value.completedAtEpochMillis)
    }

    groupedByDay.forEach { (_, dayEntries) ->
        val sortedDayEntries = dayEntries.sortedBy { it.value.completedAtEpochMillis }
        val rewardedDecks = mutableSetOf<String>()
        var rewardedCount = 0

        sortedDayEntries.forEach { indexed ->
            val completion = indexed.value
            val reward = if (
                completion.deckId in rewardedDecks ||
                rewardedCount >= maxRewardedQuizCompletionsPerDay
            ) {
                QuizCompletionReward(earnedExp = 0, earnedDiamonds = 0)
            } else {
                rewardedDecks += completion.deckId
                rewardedCount += 1
                deriveQuizCompletionReward(
                    deckId = completion.deckId,
                    completedAtEpochMillis = completion.completedAtEpochMillis,
                    totalQuestions = completion.totalQuestions,
                    answeredQuestions = completion.answeredQuestions,
                    correctAnswers = completion.correctAnswers
                )
            }
            rewardsByIndex[indexed.index] = reward
        }
    }

    return completions.indices.map { index ->
        rewardsByIndex[index] ?: QuizCompletionReward(earnedExp = 0, earnedDiamonds = 0)
    }
}

private fun epochMillisToUtcDayKey(epochMillis: Long): Long {
    val quotient = epochMillis / millisPerDay
    val remainder = epochMillis % millisPerDay
    return if (remainder < 0) quotient - 1 else quotient
}

private fun stableFnv1a(value: String): UInt {
    var hash = 2166136261u
    value.forEach { character ->
        hash = hash xor character.code.toUInt()
        hash *= 16777619u
    }
    return hash
}
