package com.brainest.presentation.introduction


import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
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

val AnticipateEasing = Easing { fraction ->
    val tension = 2.0f // Increase this number for a more exaggerated pull-back
    fraction * fraction * ((tension + 1) * fraction - tension)
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinishOnboarding: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()
    val easingToUse = AnticipateEasing // Built-in Compose easing

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5E9E1))
    ) {
//        Image(
//            imageVector = vectorResource(Res.drawable.background), // Or your general background
//            contentDescription = null,
//            modifier = Modifier.fillMaxSize(),
//            contentScale = ContentScale.Crop
//        )

        Column(
            modifier = Modifier.fillMaxSize()
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
                    // Animating Image
                    Image(
                        imageVector = vectorResource(currentStep.imageRes),
                        contentDescription = "",
                    )

                    Spacer(Modifier.height(90.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = currentStep.title,
                            style = Typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 25.sp,
                            lineHeight = 50.sp,
                            textAlign = TextAlign.Start,
                            color = Color(0xFF2C201F)

                        )

                    }
                    Spacer(modifier =Modifier.height(5.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = currentStep.description,
                            style = Typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Start
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
                        .padding(horizontal = 28.dp, vertical = 50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(35.dp))
                    BrainestButton(
                        text = if (pagerState.currentPage == onboardingPages.lastIndex) "Finish" else "Continue",
                        onClick = {
                            if (pagerState.currentPage < onboardingPages.lastIndex) {
                                scope.launch {

                                    val easingToUse = if (pagerState.currentPage == 0) {
                                        Easing { fraction ->
                                            val tension = 0.5f
                                            val t = fraction - 0.5f
                                            t * t * ((tension + 1) * t + tension) + 1.0f
                                        }
                                    } else {
                                        Easing { fraction ->
                                            val tension = 2.0f
                                            fraction * fraction * ((tension + 1) * fraction - tension)
                                        }
                                    }

                                    pagerState.animateScrollToPage(
                                        page = pagerState.currentPage + 1,
                                        animationSpec = tween(
                                            durationMillis = 1000,
                                            easing = easingToUse
                                        )
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
        OnboardingScreen(onFinishOnboarding = {})
    }
}