package com.scelio.brainest.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import brainest.feature.home.presentation.generated.resources.Res
import brainest.feature.home.presentation.generated.resources.ic_mission
import brainest.feature.home.presentation.generated.resources.ic_shop
import com.scelio.brainest.designsystem.BrainestTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HomeHeaderIconsRow(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 50.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_mission),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color.Unspecified,
            )
        }

        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_shop),
                contentDescription = null,
                modifier = Modifier.size(30.dp),
                tint = Color.Unspecified,
            )

        }
    }
}

@Preview
@Composable
private fun PreviewHomeHeaderIconsRow() {
    BrainestTheme {
        Box(
            modifier = Modifier.background(color = Color(0xFF00AD67))
        ) {
            HomeHeaderIconsRow()
        }
    }
}
