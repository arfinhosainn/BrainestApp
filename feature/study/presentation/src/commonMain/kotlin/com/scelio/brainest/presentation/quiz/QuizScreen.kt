package com.scelio.brainest.presentation.quiz

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brainest.feature.study.presentation.generated.resources.Res
import brainest.feature.study.presentation.generated.resources.quizbackground
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.BricolageGrotesq
import com.scelio.brainest.presentation.quiz.components.QuizBottomBar
import com.scelio.brainest.presentation.quiz.components.QuizOptionItem
import com.scelio.brainest.presentation.quiz.components.QuizOptionState
import com.scelio.brainest.presentation.quiz.components.QuizQuestionCard
import com.scelio.brainest.presentation.quiz.components.QuizTopAppBar
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

data class QuizOptionUi(
    val id: String,
    val text: String,
    val state: QuizOptionState = QuizOptionState.Default
)

@Composable
fun QuizScreen(
    title: String,
    questionIndex: Int,
    totalQuestions: Int,
    question: String,
    options: List<QuizOptionUi>,
    timeLeftText: String,
    totalTimeText: String,
    progress: Float,
    hintCount: Int,
    onBackClick: () -> Unit,
    onOptionClick: (String) -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onHintClick: () -> Unit,
    optionsEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(Res.drawable.quizbackground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            // 1. Custom Top Bar (Centered Title + Back Button)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            // 2. Progress Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(timeLeftText, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)

                // Custom Progress Bar
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(12.dp)
                        .background(Color.White.copy(alpha = 0.3f), CircleShape)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .background(Color.White, CircleShape)
                    )
                }

                Text(totalTimeText, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Question Index
            Text(
                text = "$questionIndex/$totalQuestions",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontFamily = BricolageGrotesq
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Question Text (Directly on background)
            Text(
                text = question,
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 23.sp,
                color = Color.White,
                fontFamily = BricolageGrotesq,
                fontWeight = FontWeight.Medium,
                lineHeight = 36.sp
            )

            Spacer(modifier = Modifier.height(48.dp)) // Fixed spacer instead of weight(1f) to allow scrolling

            // 5. Options List
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.padding(bottom = 120.dp) // Space for fixed bottom bar
            ) {
                options.forEach { option ->
                    QuizOptionItem(
                        text = option.text,
                        state = option.state,
                        onClick = { onOptionClick(option.id) },
                        enabled = optionsEnabled
                    )
                }
            }
        }

        // 6. Bottom Navigation Bar with solid background
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = Color(0xFF2DCA7E),
            shadowElevation = 0.dp // Add a slight shadow to separate it from the content
        ) {
            QuizBottomBar(
                hintCount = hintCount,
                onHintClick = onHintClick,
                onPreviousClick = onPreviousClick,
                onNextClick = onNextClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .padding(bottom = 12.dp) // Extra padding for safe area
            )
        }
    }
}

@Composable
fun QuizResultsScreen(
    totalQuestions: Int,
    answeredQuestions: Int,
    correctAnswers: Int,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scorePercent = if (totalQuestions == 0) 0 else (correctAnswers * 100) / totalQuestions

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            QuizTopAppBar(
                title = "Quiz results",
                timeLeftText = "$answeredQuestions answered",
                totalTimeText = "$totalQuestions total",
                progress = 1f,
                onBackClick = onBackClick
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                QuizQuestionCard(
                    title = "$scorePercent%",
                    subtitle = "You got $correctAnswers out of $totalQuestions correct"
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatSummary(label = "Answered", value = answeredQuestions.toString())
                    StatSummary(label = "Correct", value = correctAnswers.toString())
                    StatSummary(label = "Wrong", value = (answeredQuestions - correctAnswers).coerceAtLeast(0).toString())
                }
            }

            Button(
                onClick = onRetryClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(text = "Retry quiz")
            }
        }
    }
}

@Composable
private fun StatSummary(
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview
@Composable
private fun PreviewQuizScreen() {
    BrainestTheme {
        Surface {
            QuizScreen(
                title = "Playing quiz",
                questionIndex = 14,
                totalQuestions = 20,
                question = "With over 222 million units sold, what is Apple's highest-selling iPhone model?",
                options = listOf(
                    QuizOptionUi(
                        id = "a",
                        text = "iPhone 6/6 Plus",
                        state = QuizOptionState.Incorrect
                    ),
                    QuizOptionUi(
                        id = "b",
                        text = "iPhone 8",
                        state = QuizOptionState.Default
                    ),
                    QuizOptionUi(
                        id = "c",
                        text = "iPhone XR",
                        state = QuizOptionState.Correct
                    ),
                    QuizOptionUi(
                        id = "d",
                        text = "iPhone 11 Pro",
                        state = QuizOptionState.Default
                    )
                ),
                timeLeftText = "05:42",
                totalTimeText = "10:00",
                progress = 0.58f,
                hintCount = 5,
                onBackClick = {},
                onOptionClick = {},
                onPreviousClick = {},
                onNextClick = {},
                onHintClick = {},
                optionsEnabled = true
            )
        }
    }
}
