package com.scelio.brainest.presentation.quiz

import androidx.compose.foundation.Image
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brainest.feature.study.presentation.generated.resources.Res
import brainest.feature.study.presentation.generated.resources.ic_25coin
import brainest.feature.study.presentation.generated.resources.ic_diamond
import brainest.feature.study.presentation.generated.resources.ic_stage
import brainest.feature.study.presentation.generated.resources.quiz_completion_continue
import brainest.feature.study.presentation.generated.resources.quiz_completion_correct_label
import brainest.feature.study.presentation.generated.resources.quiz_completion_earned_label
import brainest.feature.study.presentation.generated.resources.quiz_completion_exp_format
import brainest.feature.study.presentation.generated.resources.quiz_completion_reward_prefix
import brainest.feature.study.presentation.generated.resources.quiz_completion_reward_suffix
import brainest.feature.study.presentation.generated.resources.quiz_completion_title
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.BricolageGrotesq
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun QuizResultsScreen(
    totalQuestions: Int,
    answeredQuestions: Int,
    correctAnswers: Int,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stageImageHeight = 420.dp
    val stageCoinVerticalOffset = stageImageHeight * 0.080f
    val coinAlpha = remember { Animatable(0f) }
    val totalForStats = totalQuestions.coerceAtLeast(answeredQuestions).coerceAtLeast(1)
    val normalizedScore = correctAnswers.coerceAtLeast(0).coerceAtMost(totalForStats)
    val earnedExp = (normalizedScore * 200) / totalForStats
    val diamondReward = (normalizedScore * 40) / totalForStats

    LaunchedEffect(Unit) {
        coinAlpha.snapTo(0f)
        coinAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 650,
                easing = FastOutSlowInEasing
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF3F3F3))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(stageImageHeight)
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_stage),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.TopCenter
            )
            Icon(
                painter = painterResource(Res.drawable.ic_25coin),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = stageCoinVerticalOffset)
                    .alpha(coinAlpha.value),
                tint = Color.Unspecified
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(Res.string.quiz_completion_title),
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF233333),
                fontFamily = BricolageGrotesq
            )

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF1F1F1),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color(0xFFDFDFDF),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 18.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(Res.string.quiz_completion_correct_label),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF6C6C6C),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$normalizedScore/$totalForStats",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF25BE73),
                        fontFamily = BricolageGrotesq
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(54.dp)
                        .background(Color(0xFFD7D7D7))
                )

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(Res.string.quiz_completion_earned_label),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6C6C6C),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(Res.string.quiz_completion_exp_format, earnedExp),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF25BE73),
                        fontFamily = BricolageGrotesq
                    )
                }
            }

            Spacer(modifier = Modifier.height(35.dp))

            Row(
                modifier = Modifier
                    .background(
                        color = Color(0xFFF1E8D4),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 22.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(Res.string.quiz_completion_reward_prefix),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4E544D),
                )
                Text(
                    text = diamondReward.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF9640),
                    fontFamily = BricolageGrotesq
                )
                Icon(
                    painter = painterResource(Res.drawable.ic_diamond),
                    contentDescription = null,
                    modifier = Modifier.height(22.dp),
                    tint = Color.Unspecified
                )
                Text(
                    text = stringResource(Res.string.quiz_completion_reward_suffix),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4E544D),
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        color = Color(0xFF23C376),
                        shape = RoundedCornerShape(30.dp)
                    )
                    .clickable(onClick = onContinueClick),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 14.dp, top = 7.dp)
                        .height(4.dp)
                        .width(4.dp)
                        .background(Color.White.copy(alpha = 0.85f), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 14.dp, top = 7.dp)
                        .height(4.dp)
                        .width(4.dp)
                        .background(Color.White.copy(alpha = 0.85f), CircleShape)
                )
                Text(
                    text = stringResource(Res.string.quiz_completion_continue),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontFamily = BricolageGrotesq,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewQuizResultsScreen() {
    BrainestTheme {
        Surface {
            QuizResultsScreen(
                totalQuestions = 6,
                answeredQuestions = 6,
                correctAnswers = 6,
                onContinueClick = {}
            )
        }
    }
}
