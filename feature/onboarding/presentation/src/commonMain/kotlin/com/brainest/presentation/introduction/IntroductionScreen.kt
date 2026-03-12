package com.brainest.presentation.introduction


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.Typography
import com.scelio.brainest.designsystem.components.buttons.BrainestButton
import com.scelio.brainest.designsystem.components.buttons.BrainestButtonStyle
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IntroductionScreen(onFinishOnboarding: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5E9E1))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                val currentStep = onboardingPages[page]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        imageVector = vectorResource(currentStep.imageRes),
                        contentDescription = "",
                    )
                    Spacer(Modifier.height(90.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = currentStep.title,
                            style = Typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 28.sp,
                            lineHeight = 28.sp,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF2C201F)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = currentStep.description,
                            style = Typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = Color.Transparent
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(35.dp))
                    BrainestButton(
                        text = if (pagerState.currentPage == onboardingPages.lastIndex) "Finish" else "Continue",
                        onClick = {
                            if (pagerState.currentPage < onboardingPages.lastIndex) {
                                scope.launch {

                                    pagerState.animateScrollToPage(
                                        page = pagerState.currentPage + 1
                                    )
                                }
                            } else {
                                onFinishOnboarding()
                            }
                        },
                        textStyles = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Typography.bodyMedium.fontFamily,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null
                            )
                        },
                        style = BrainestButtonStyle.PRIMARY
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun PreviewOnboardingScreen() {
    BrainestTheme {
        IntroductionScreen(onFinishOnboarding = {})
    }
}
