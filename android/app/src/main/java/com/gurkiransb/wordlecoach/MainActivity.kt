package com.gurkiransb.wordlecoach

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

private val Ink = Color(0xFF202822)
private val Paper = Color(0xFFF6F4EC)
private val Green = Color(0xFF5B9361)
private val Yellow = Color(0xFFC9A94A)
private val Gray = Color(0xFF858A86)
private val TileBorder = Color(0xFFD5D8CF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Green,
                    onPrimary = Color.White,
                    background = Paper,
                    surface = Color.White,
                    onSurface = Ink,
                    onBackground = Ink,
                ),
            ) {
                WordleCoachApp(
                    readWords = {
                        assets.open("words.txt").bufferedReader().useLines { lines ->
                            lines.map(String::trim)
                                .filter { it.length == WordleSolver.WORD_LENGTH }
                                .map(String::lowercase)
                                .toList()
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun WordleCoachApp(readWords: suspend () -> List<String>) {
    var dictionary by remember { mutableStateOf<List<String>>(emptyList()) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var guess by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf<String?>(null) }
    val feedback = remember {
        mutableStateListOf(*Array(WordleSolver.WORD_LENGTH) { TileColor.Gray })
    }
    val history = remember { mutableStateListOf<GuessFeedback>() }
    val historySnapshot = history.toList()
    val result = remember(dictionary, historySnapshot) {
        if (dictionary.isEmpty()) null else WordleSolver.solve(dictionary, historySnapshot)
    }
    val dictionarySet = remember(dictionary) { dictionary.toHashSet() }
    val isValidGuess = guess.length == WordleSolver.WORD_LENGTH && guess in dictionarySet

    LaunchedEffect(Unit) {
        try {
            val words = withContext(Dispatchers.IO) { readWords() }
            if (words.isEmpty()) {
                loadError = "The bundled word list is empty. Please reinstall the app and try again."
            } else {
                dictionary = words
            }
        } catch (error: IOException) {
            loadError = "The word list could not be loaded. Please reinstall the app and try again."
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = Paper,
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(16.dp))
            Header(onReset = {
                guess = ""
                feedback.indices.forEach { feedback[it] = TileColor.Gray }
                history.clear()
                inputError = null
            })
            Spacer(Modifier.height(20.dp))

            when {
                loadError != null -> StatusCard(loadError!!)
                isLoading -> LoadingCard()
                else -> {
                    result?.let { currentResult ->
                        SuggestionCard(
                            suggestion = currentResult.suggestion,
                            candidateCount = currentResult.candidates.size,
                            onUseSuggestion = { word ->
                                guess = word
                                feedback.indices.forEach { feedback[it] = TileColor.Gray }
                                inputError = null
                            },
                        )
                        Spacer(Modifier.height(16.dp))
                    }

                    EntryCard(
                        guess = guess,
                        feedback = feedback,
                        inputError = inputError,
                        onGuessChange = { value ->
                            guess = value.filter(Char::isLetter).take(WordleSolver.WORD_LENGTH)
                                .lowercase()
                            inputError = null
                        },
                        onFeedbackChange = { index -> feedback[index] = feedback[index].next() },
                        onSubmit = {
                            if (!isValidGuess) {
                                inputError = "Enter a valid five-letter word from the dictionary."
                            } else {
                                history.add(GuessFeedback(guess, feedback.toList()))
                                guess = ""
                                feedback.indices.forEach { feedback[it] = TileColor.Gray }
                                inputError = null
                            }
                        },
                    )
                    Spacer(Modifier.height(16.dp))

                    if (history.isNotEmpty()) {
                        HistoryCard(history)
                        Spacer(Modifier.height(16.dp))
                    }

                    result?.let { currentResult ->
                        CandidateCard(currentResult.candidates)
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
            Text(
                text = "A little brainpower for your next green row.",
                color = Ink.copy(alpha = 0.58f),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun Header(onReset: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("WORD PUZZLE", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Ink)
                Text(" ✦", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Yellow)
            }
            Text(
                "COACH  ·  YOUR DAILY BRAIN BOOST",
                fontSize = 10.sp,
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.SemiBold,
                color = Ink.copy(alpha = 0.58f),
            )
        }
        TextButton(onClick = onReset) {
            Text("New game", color = Green, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SuggestionCard(
    suggestion: String?,
    candidateCount: Int,
    onUseSuggestion: (String) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F0E4)),
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Surface(color = Color.White.copy(alpha = 0.8f), shape = CircleShape) {
                    Text("✦", modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), color = Green)
                }
                Text(
                    if (candidateCount == 1) "ONLY ONE ANSWER LEFT" else "YOUR NEXT BEST GUESS",
                    fontSize = 11.sp,
                    letterSpacing = 1.1.sp,
                    fontWeight = FontWeight.Bold,
                    color = Green,
                )
            }
            Spacer(Modifier.height(10.dp))
            if (suggestion == null) {
                Text(
                    "No words fit those tiles. Double-check your colors.",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Ink,
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    AnimatedVisibility(
                        visible = suggestion != null,
                        enter = fadeIn() + scaleIn(),
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            suggestion.orEmpty().forEach { letter ->
                                SuggestionTile(letter = letter)
                            }
                        }
                    }
                    Text(
                        "$candidateCount\n${if (candidateCount == 1) "answer" else "answers"} left",
                        color = Ink.copy(alpha = 0.64f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.End,
                        lineHeight = 16.sp,
                    )
                }
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { onUseSuggestion(suggestion) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Green),
                ) {
                    Text("Use this guess", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SuggestionTile(letter: Char) {
    Box(
        modifier = Modifier
            .size(43.dp)
            .background(Green, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            letter.uppercase(),
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 21.sp,
        )
    }
}

@Composable
private fun EntryCard(
    guess: String,
    feedback: List<TileColor>,
    inputError: String?,
    onGuessChange: (String) -> Unit,
    onFeedbackChange: (Int) -> Unit,
    onSubmit: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "HOW DID YOUR GUESS GO?",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold,
                color = Ink.copy(alpha = 0.65f),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = guess,
                onValueChange = onGuessChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Your five-letter guess") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = { onSubmit() }),
                shape = RoundedCornerShape(14.dp),
            )
            Spacer(Modifier.height(15.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                feedback.forEachIndexed { index, color ->
                    FeedbackTile(
                        letter = guess.getOrNull(index)?.uppercase() ?: "",
                        color = color,
                        index = index,
                        onClick = { onFeedbackChange(index) },
                    )
                }
            }
            Spacer(Modifier.height(9.dp))
            Text(
                "Tap each tile to match your game: gray → yellow → green",
                fontSize = 11.sp,
                color = Ink.copy(alpha = 0.58f),
                textAlign = TextAlign.Center,
            )
            AnimatedVisibility(visible = inputError != null, enter = fadeIn() + scaleIn()) {
                Text(
                    text = inputError.orEmpty(),
                    modifier = Modifier.padding(top = 10.dp),
                    color = Color(0xFFB34335),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(Modifier.height(14.dp))
            Button(
                onClick = onSubmit,
                enabled = guess.length == WordleSolver.WORD_LENGTH,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(15.dp),
            ) {
                Text("Add feedback", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun FeedbackTile(
    letter: String,
    color: TileColor,
    index: Int,
    onClick: () -> Unit,
) {
    val targetColor = when (color) {
        TileColor.Gray -> Gray
        TileColor.Yellow -> Yellow
        TileColor.Green -> Green
    }
    val backgroundColor by animateColorAsState(
        targetValue = if (letter.isEmpty()) Color.White else targetColor,
        label = "feedback-tile-color-$index",
    )
    Box(
        modifier = Modifier
            .size(52.dp)
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .then(
                if (letter.isEmpty()) {
                    Modifier.border(2.dp, TileBorder, RoundedCornerShape(12.dp))
                } else {
                    Modifier
                },
            )
            .clickable(role = Role.Button, onClick = onClick)
            .semantics {
                role = Role.Button
                contentDescription =
                    "${letter.ifEmpty { "Empty" }}, ${color.name.lowercase()}. Tap to change color."
            },
        contentAlignment = Alignment.Center,
    ) {
        if (letter.isNotEmpty()) {
            Text(letter, fontSize = 23.sp, fontWeight = FontWeight.Black, color = Color.White)
        }
    }
}

@Composable
private fun HistoryCard(history: List<GuessFeedback>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(Modifier.padding(17.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionTitle("YOUR CLUE TRAIL", "${history.size} ${if (history.size == 1) "guess" else "guesses"}")
            history.forEach { entry ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(entry.guess.uppercase(), fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        entry.colors.forEach { color ->
                            Box(
                                Modifier
                                    .size(22.dp)
                                    .background(color.toComposeColor(), RoundedCornerShape(6.dp)),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CandidateCard(candidates: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(Modifier.padding(17.dp)) {
            SectionTitle("POSSIBLE ANSWERS", "${candidates.size} found")
            if (candidates.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    candidates.take(18).joinToString("   ") { it.uppercase() } +
                        if (candidates.size > 18) "   …" else "",
                    color = Ink,
                    fontSize = 14.sp,
                    lineHeight = 26.sp,
                    letterSpacing = 0.5.sp,
                )
            } else {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Try changing a tile color or starting a fresh game.",
                    color = Ink.copy(alpha = 0.65f),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String, detail: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(title, fontSize = 11.sp, letterSpacing = 0.9.sp, fontWeight = FontWeight.Bold)
        Text(detail, fontSize = 11.sp, color = Green, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun LoadingCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp, color = Green)
            Text("  Getting your word list ready…", color = Ink)
        }
    }
}

@Composable
private fun StatusCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Text(message, modifier = Modifier.padding(20.dp), color = Color(0xFFB34335))
    }
}

private fun TileColor.toComposeColor(): Color = when (this) {
    TileColor.Gray -> Gray
    TileColor.Yellow -> Yellow
    TileColor.Green -> Green
}
