package com.scelio.brainest.designsystem.components.navbar.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import brainest.core.designsystem.generated.resources.Res
import brainest.core.designsystem.generated.resources.turtle
import brainest.core.designsystem.generated.resources.turtle_smile
import com.scelio.brainest.designsystem.BrainestTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProfileStatsOverlay(
    decks: Int,
    quizzes: Int,
    others: Int,
    modifier: Modifier = Modifier
) {
    val cardHeight = 88.dp       // approximate stats card height
    val turtleHeight = 260.dp
    // Total box height = turtle height + visible portion of card (80%).
    val totalHeight = turtleHeight + (cardHeight * 0.80f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(totalHeight)
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Turtle — bottom-aligned, 80% above card top, 20% overlaps below
        Image(
            painter = painterResource(resource = Res.drawable.turtle),
            contentDescription = "Mascot",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(turtleHeight)
                .align(Alignment.BottomCenter)
                .offset(y = -(cardHeight * 0.80f)) // shift up by 80% of card height
                .zIndex(0f)
        )

        // Stats card — pinned to bottom
        StatsCard(
            decks = decks,
            quizzes = quizzes,
            others = others,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .align(Alignment.BottomCenter)
                .zIndex(1f)
        )
    }
}
@Preview
@Composable
fun PreviewProfileStatsOverlay() {
    BrainestTheme {
        ProfileStatsOverlay(decks = 10, quizzes = 20, others = 30)
    }

}
