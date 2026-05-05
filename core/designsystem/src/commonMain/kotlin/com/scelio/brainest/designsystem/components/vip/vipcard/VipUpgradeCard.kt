package com.scelio.brainest.designsystem.components.vip.vipcard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brainest.core.designsystem.generated.resources.Res
import brainest.core.designsystem.generated.resources.ic_grass_crown
import brainest.core.designsystem.generated.resources.upgrade_to
import brainest.core.designsystem.generated.resources.vip_account
import com.scelio.brainest.designsystem.BrainestTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun VipUpgradeCard(
    modifier: Modifier = Modifier,
    titleLineOne: String = stringResource(Res.string.upgrade_to),
    titleLineTwo: String = stringResource(Res.string.vip_account),
) {
    val cardShape = RoundedCornerShape(40.dp)
    val cardColor = Color(0xFF7BCB63)
    val accentDark = Color(0xFF69B453)
    val accentLight = Color(0xFF8ED978)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp), // ✅ Increased height
        shape = cardShape,
        color = cardColor,
        shadowElevation = 0.dp,
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Blobs


            // Text — left side
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 20.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = titleLineOne,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = Color.White,
                )
                Text(
                    text = titleLineTwo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = Color.White,
                )
            }

            // Crown — flush to bottom-end, offset downward so it bleeds out ✅
            Image(
                imageVector = vectorResource(Res.drawable.ic_grass_crown),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
            )
        }
    }
}

@Preview
@Composable
private fun PreviewVipUpgradeCard() {
    BrainestTheme {
        VipUpgradeCard(
            modifier = Modifier.padding(16.dp)
        )
    }
}
