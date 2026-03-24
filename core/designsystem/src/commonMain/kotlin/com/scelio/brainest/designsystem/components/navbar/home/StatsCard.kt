package com.scelio.brainest.designsystem.components.navbar.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scelio.brainest.designsystem.BrainestTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun StatsCard(
    decks: Int,
    quizzes: Int,
    others: Int,
    modifier: Modifier = Modifier
) {
    val cardGreen = Color(0xFF3DBE72)

    Row(
        modifier = modifier
            .background(color = cardGreen, shape = RoundedCornerShape(20.dp))
            .padding(vertical = 20.dp, horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatItem(label = "Decks", value = decks.toString())

        VerticalDivider(
            modifier = Modifier.height(48.dp),
            thickness = 1.dp,
            color = Color.White.copy(alpha = 0.4f)
        )

        StatItem(label = "Quizzes", value = quizzes.toString())

        VerticalDivider(
            modifier = Modifier.height(48.dp),
            thickness = 1.dp,
            color = Color.White.copy(alpha = 0.4f)
        )

        StatItem(label = "Others", value = others.toString())
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
@Preview
@Composable
fun PreviewStatsCard(){
    BrainestTheme {
        StatsCard(
            decks = 15,
            quizzes = 490,
            others = 23
        )

    }
}