package com.gurkiransb.wordlecoach

enum class TileColor {
    Gray,
    Yellow,
    Green;

    fun next(): TileColor = when (this) {
        Gray -> Yellow
        Yellow -> Green
        Green -> Gray
    }
}

data class GuessFeedback(
    val guess: String,
    val colors: List<TileColor>,
)

data class SolverResult(
    val candidates: List<String>,
    val suggestion: String?,
    val suggestionScore: Int,
)

object WordleSolver {
    const val WORD_LENGTH = 5

    fun filter(words: List<String>, feedbackHistory: List<GuessFeedback>): List<String> =
        words.filter { candidate ->
            candidate.length == WORD_LENGTH &&
                feedbackHistory.all { feedback -> matches(candidate, feedback) }
        }

    fun matches(candidate: String, feedback: GuessFeedback): Boolean {
        if (candidate.length != WORD_LENGTH ||
            feedback.guess.length != WORD_LENGTH ||
            feedback.colors.size != WORD_LENGTH
        ) {
            return false
        }

        val actualColors = MutableList(WORD_LENGTH) { TileColor.Gray }
        val remainingLetters = mutableMapOf<Char, Int>()

        for (position in 0 until WORD_LENGTH) {
            if (candidate[position] == feedback.guess[position]) {
                actualColors[position] = TileColor.Green
            } else {
                val letter = candidate[position]
                remainingLetters[letter] = remainingLetters.getOrDefault(letter, 0) + 1
            }
        }

        for (position in 0 until WORD_LENGTH) {
            val letter = feedback.guess[position]
            if (actualColors[position] != TileColor.Green) {
                val remainingCount = remainingLetters.getOrDefault(letter, 0)
                if (remainingCount > 0) {
                    actualColors[position] = TileColor.Yellow
                    remainingLetters[letter] = remainingCount - 1
                }
            }
        }

        return actualColors == feedback.colors
    }

    fun solve(words: List<String>, feedbackHistory: List<GuessFeedback>): SolverResult {
        val candidates = filter(words, feedbackHistory)
        if (candidates.isEmpty()) return SolverResult(candidates, null, 0)

        val frequencies = IntArray(26)
        for (word in candidates) {
            for (letter in word) {
                if (letter in 'a'..'z') frequencies[letter - 'a']++
            }
        }

        val scored = candidates.map { word ->
            word to word.toSet().sumOf { letter ->
                if (letter in 'a'..'z') frequencies[letter - 'a'] else 0
            }
        }
        val (suggestion, score) = scored.maxBy { it.second }
        return SolverResult(candidates, suggestion, score)
    }
}
