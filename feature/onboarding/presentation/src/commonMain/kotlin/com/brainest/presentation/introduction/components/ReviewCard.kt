package com.brainest.presentation.introduction.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brainest.feature.onboarding.presentation.generated.resources.Res
import brainest.feature.onboarding.presentation.generated.resources.star
import com.scelio.brainest.designsystem.BrainestTheme
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ReviewCard(
    title: String,
    reviewerName: String,
    rating: Int,
    content: String,
    modifier: Modifier = Modifier // Add this
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier // Use it here
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            // Header: Title
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D2D)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Rating and Reviewer Name
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    repeat(5) { index ->
                        Icon(
                            imageVector = vectorResource(Res.drawable.star),
                            contentDescription = null,
                            tint = if (index < rating) Color(0xFFFFC107) else Color.LightGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = reviewerName,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color(0xFF4A4A4A)
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Review Content
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 24.sp,
                    color = Color(0xFF757575)
                )
            )
        }
    }
}

@Preview
@Composable
fun PreviewReviewCard() {
    BrainestTheme {
        ReviewCard(
            title = "Exactly what I needed!",
            reviewerName = "Felicia2",
            rating = 5,
            content = "Simple, fast, and effective! I have reached the heaviest weight of my life and was feeling pretty hopeless. This app is simple, ADHD friendly, and helps me to really be aware of the foods I eat. I'm already down 4 lbs in a week and I'm not starving myself, just eating healthier and with awareness!"
        )

    }
}