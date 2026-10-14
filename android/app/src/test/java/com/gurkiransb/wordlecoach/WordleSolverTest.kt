package com.gurkiransb.wordlecoach

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WordleSolverTest {
    @Test
    fun grayDuplicateCapsLetterCountAtKnownMatches() {
        val feedback = GuessFeedback(
            guess = "allee",
            colors = listOf(
                TileColor.Green,
                TileColor.Yellow,
                TileColor.Gray,
                TileColor.Gray,
                TileColor.Gray,
            ),
        )

        assertTrue(WordleSolver.matches("axial", feedback))
        assertFalse(WordleSolver.matches("apple", feedback))
        assertFalse(WordleSolver.matches("alley", feedback))
    }

    @Test
    fun yellowRequiresLetterInDifferentPosition() {
        val feedback = GuessFeedback(
            guess = "crane",
            colors = listOf(
                TileColor.Yellow,
                TileColor.Gray,
                TileColor.Gray,
                TileColor.Gray,
                TileColor.Gray,
            ),
        )

        assertTrue(WordleSolver.matches("sculp", feedback))
        assertFalse(WordleSolver.matches("crisp", feedback))
    }

    @Test
    fun suggestionIsNullWhenNoCandidatesRemain() {
        val result = WordleSolver.solve(
            words = listOf("apple"),
            feedbackHistory = listOf(
                GuessFeedback("crane", List(WordleSolver.WORD_LENGTH) { TileColor.Green }),
            ),
        )

        assertEquals(emptyList<String>(), result.candidates)
        assertEquals(null, result.suggestion)
    }
}
