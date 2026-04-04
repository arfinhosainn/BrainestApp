package com.scelio.brainest.presentation.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.presentation.quiz.components.QuizBottomBar
import com.scelio.brainest.presentation.quiz.components.QuizOptionItem
import com.scelio.brainest.presentation.quiz.components.QuizOptionState
import com.scelio.brainest.presentation.quiz.components.QuizTopAppBar
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
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Gray)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(colorScheme.primaryContainer.copy(alpha = 0.2f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .padding(bottom = 96.dp)
        ) {
            QuizTopAppBar(
                title = title,
                timeLeftText = timeLeftText,
                totalTimeText = totalTimeText,
                progress = progress,
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(33.dp))

            Text(
                text = "$questionIndex/$totalQuestions",
                fontSize = 16.sp,
                color = colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = question,
                fontSize = 23.sp,
                color = colorScheme.onSurface,
                lineHeight = 30.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(80.dp))

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                options.forEach { option ->
                    QuizOptionItem(
                        text = option.text,
                        state = option.state,
                        onClick = { onOptionClick(option.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        QuizBottomBar(
            hintCount = hintCount,
            onHintClick = onHintClick,
            onPreviousClick = onPreviousClick,
            onNextClick = onNextClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
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
                onHintClick = {}
            )
        }
    }
}
