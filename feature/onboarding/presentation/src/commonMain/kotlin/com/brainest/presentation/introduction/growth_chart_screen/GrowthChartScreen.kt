package com.brainest.presentation.introduction.growth_chart_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brainest.feature.onboarding.presentation.generated.resources.Res
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.BricolageGrotesq
import com.scelio.brainest.designsystem.Typography
import com.scelio.brainest.designsystem.components.buttons.BrainestButton
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun GrowthChartScreen(
    onContinueClick: () -> Unit = {}
) {
    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/growth.json").decodeToString()
        )
    }
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = Int.MAX_VALUE
    )

    Column(

        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5E9E1))
            .padding(horizontal = 24.dp)
            .padding(top = 60.dp, bottom = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween

    ) {

        Card(

            modifier = Modifier
                .size(320.dp), // fills all available vertical space
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)

        ) {

            Column(

                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 5.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top

            ) {


                Text(
                    text = "Brainest Gives Your Wings",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontFamily = BricolageGrotesq,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp

                )


                Spacer(modifier = Modifier.height(5.dp))
                Image(

                    painter = rememberLottiePainter(
                        composition = composition,
                        progress = { progress }

                    ),

                    contentDescription = "Growth chart animation",
                    modifier = Modifier

                        .fillMaxWidth()

                        .weight(1f)

                )

            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "This is your chance to win",
                style = TextStyle(
                    fontFamily = BricolageGrotesq,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 36.sp,
                    color = Color(0xFF2C201F)
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp)) // Gap between heading and body text

            Text(
                text = "This is how brainest help you to win in life and gives you so much you get A+ in exam",
                style = TextStyle(
                    fontFamily = BricolageGrotesq,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 24.sp, // slightly increased line height for readability
                    color = Color(0xFF2C201F)
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        BrainestButton(
            text = "Continue",
            onClick = onContinueClick,
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
    }
}

@Preview
@Composable
fun PreviewGrowthChartScreen() {
    BrainestTheme(darkTheme = true) {
        GrowthChartScreen()
    }
}
