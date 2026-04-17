package com.brainest.presentation.introduction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brainest.feature.onboarding.presentation.generated.resources.Res
import brainest.feature.onboarding.presentation.generated.resources.app_store_rating
import brainest.feature.onboarding.presentation.generated.resources.continue_label
import brainest.feature.onboarding.presentation.generated.resources.join_others
import brainest.feature.onboarding.presentation.generated.resources.rating_score
import brainest.feature.onboarding.presentation.generated.resources.review1_content
import brainest.feature.onboarding.presentation.generated.resources.review1_name
import brainest.feature.onboarding.presentation.generated.resources.review1_title
import brainest.feature.onboarding.presentation.generated.resources.review2_content
import brainest.feature.onboarding.presentation.generated.resources.review2_name
import brainest.feature.onboarding.presentation.generated.resources.review2_title
import brainest.feature.onboarding.presentation.generated.resources.review3_content
import brainest.feature.onboarding.presentation.generated.resources.review3_name
import brainest.feature.onboarding.presentation.generated.resources.review3_title
import brainest.feature.onboarding.presentation.generated.resources.star
import brainest.feature.onboarding.presentation.generated.resources.trusted_by_thousands
import com.brainest.presentation.introduction.components.ReviewCard
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.BricolageGrotesq
import com.scelio.brainest.designsystem.Typography
import com.scelio.brainest.designsystem.components.buttons.BrainestButton
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun UserReviewScreen(
    onContinueClick: () -> Unit
) {
    Scaffold(
        bottomBar = {
            // The BottomBar stays fixed while the content above scrolls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5E9E1)) // Match background to prevent clipping look
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BrainestButton(
                    text = stringResource(Res.string.continue_label),
                    onClick = onContinueClick,
                    enabled = true,
                    textStyles = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Typography.bodyMedium.fontFamily,
                        color = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(Res.string.join_others),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5E9E1))
                .padding(innerPadding) // Important: Respect Scaffold padding
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // --- Header Section ---
            Text(
                text = stringResource(Res.string.trusted_by_thousands),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontFamily = BricolageGrotesq
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(Res.string.rating_score),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )
                repeat(5) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.star),
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = stringResource(Res.string.app_store_rating),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- Cards Section ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Primary Focused Card
                ReviewCard(
                    title = stringResource(Res.string.review1_title),
                    reviewerName = stringResource(Res.string.review1_name),
                    rating = 5,
                    content = stringResource(Res.string.review1_content),
                    modifier = Modifier.fillMaxWidth()
                )

                // Secondary Faded Card 1
                ReviewCard(
                    title = stringResource(Res.string.review2_title),
                    reviewerName = stringResource(Res.string.review2_name),
                    rating = 5,
                    content = stringResource(Res.string.review2_content),
                    modifier = Modifier
                        .fillMaxWidth()

                )

                // Secondary Faded Card 2 (To ensure scrolling is visible)
                ReviewCard(
                    title = stringResource(Res.string.review3_title),
                    reviewerName = stringResource(Res.string.review3_name),
                    rating = 5,
                    content = stringResource(Res.string.review3_content),
                    modifier = Modifier
                        .fillMaxWidth()

                )
            }

            // Extra spacer at the bottom so the last card isn't
            // completely hidden by the bottom bar's padding
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Preview
@Composable
fun PreviewUserReviewScreen(){
    BrainestTheme {
        UserReviewScreen(onContinueClick = {})
    }
}
