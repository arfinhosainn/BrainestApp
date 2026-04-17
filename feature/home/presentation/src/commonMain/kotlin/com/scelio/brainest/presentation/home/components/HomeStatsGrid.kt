package com.scelio.brainest.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import brainest.feature.home.presentation.generated.resources.Res
import brainest.feature.home.presentation.generated.resources.home_bronze_class_label
import brainest.feature.home.presentation.generated.resources.home_lessons_label
import brainest.feature.home.presentation.generated.resources.home_streak_label
import brainest.feature.home.presentation.generated.resources.home_vocabulary_label
import brainest.feature.home.presentation.generated.resources.ic_bronze
import brainest.feature.home.presentation.generated.resources.ic_lesson
import brainest.feature.home.presentation.generated.resources.ic_vocab
import brainest.feature.home.presentation.generated.resources.ic_yellow_fire
import com.scelio.brainest.designsystem.BrainestTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HomeStatsGrid(
    stats: List<HomeStatCardUi>,
    modifier: Modifier = Modifier,
) {
    val rows = remember(stats) { stats.chunked(2) }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                val first = row.firstOrNull()
                val second = row.getOrNull(1)

                if (first != null) {
                    HomeStatCard(
                        stat = first,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                    )
                }
                if (second != null) {
                    HomeStatCard(
                        stat = second,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewHomeStatsGrid() {
    BrainestTheme {
        HomeStatsGrid(
            stats = listOf(
                HomeStatCardUi(
                    value = "2 Days",
                    label = stringResource(Res.string.home_streak_label),
                    icon = Res.drawable.ic_yellow_fire,
                ),
                HomeStatCardUi(
                    value = "2 / 490",
                    label = stringResource(Res.string.home_lessons_label),
                    icon = Res.drawable.ic_lesson,
                ),
                HomeStatCardUi(
                    value = "3 words",
                    label = stringResource(Res.string.home_vocabulary_label),
                    icon = Res.drawable.ic_vocab,
                ),
                HomeStatCardUi(
                    value = "138 P",
                    label = stringResource(Res.string.home_bronze_class_label),
                    icon = Res.drawable.ic_bronze,
                ),
            ),
            modifier = Modifier,
        )
    }
}
