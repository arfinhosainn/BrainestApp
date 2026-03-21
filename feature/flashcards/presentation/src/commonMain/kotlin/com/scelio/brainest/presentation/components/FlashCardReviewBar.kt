package com.scelio.brainest.presentation.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

// ---------------------------------------------------------------------------
// Colors — tweak to match your theme
// ---------------------------------------------------------------------------

private val ColorKnowBackground    = Color(0xFFE8FAE8)   // light green tint
private val ColorKnowAccent        = Color(0xFF4CAF50)   // green number
private val ColorDontKnowBackground = Color(0xFFF2F2F2)  // light grey tint
private val ColorDontKnowAccent    = Color(0xFF9E9E9E)   // grey number
private val ColorKnowButton        = Color(0xFF66E060)   // bright green CTA
private val ColorDontKnowButton    = Color(0xFFE8E8E8)   // soft grey CTA

// ---------------------------------------------------------------------------
// Count tile
// ---------------------------------------------------------------------------


@Composable
fun ReviewCountTile(
    count:           Int,
    label:           String,
    backgroundColor: Color,
    countColor:      Color,
    labelColor:      Color,
    modifier:        Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(vertical = 16.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text       = count.toString(),
            fontSize   = 28.sp,
            fontWeight = FontWeight.Bold,
            color      = countColor
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text       = label,
            fontSize   = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            color      = labelColor
        )
    }
}

// ---------------------------------------------------------------------------
// Full review action bar  (count tiles + action buttons)
// ---------------------------------------------------------------------------


@Composable
fun FlashCardReviewBar(
    knowCount:     Int,
    dontKnowCount: Int,
    onKnow:        () -> Unit,
    onDontKnow:    () -> Unit,
    modifier:      Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // ── Count tiles row ───────────────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ReviewCountTile(
                count           = dontKnowCount,
                label           = "DON'T KNOW",
                backgroundColor = ColorDontKnowBackground,
                countColor      = ColorDontKnowAccent,
                labelColor      = ColorDontKnowAccent,
                modifier        = Modifier.weight(1f)
            )
            ReviewCountTile(
                count           = knowCount,
                label           = "KNOW",
                backgroundColor = ColorKnowBackground,
                countColor      = ColorKnowAccent,
                labelColor      = ColorKnowAccent,
                modifier        = Modifier.weight(1f)
            )
        }

        // ── Action buttons row ────────────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // DON'T KNOW button
            Button(
                onClick        = onDontKnow,
                modifier       = Modifier
                    .weight(1f)
                    .height(60.dp),
                shape          = RoundedCornerShape(50.dp),
                colors         = ButtonDefaults.buttonColors(
                    containerColor = ColorDontKnowButton,
                    contentColor   = Color(0xFF333333)
                ),
                elevation      = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text(
                    text       = "DON'T KNOW",
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            // KNOW button
            Button(
                onClick        = onKnow,
                modifier       = Modifier
                    .weight(1f)
                    .height(60.dp),
                shape          = RoundedCornerShape(50.dp),
                colors         = ButtonDefaults.buttonColors(
                    containerColor = ColorKnowButton,
                    contentColor   = Color.White
                ),
                elevation      = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text(
                    text       = "KNOW",
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@Preview(name = "ReviewBar — mid session", showBackground = true)
@Composable
private fun PreviewReviewBarMid() {
    MaterialTheme {
        Surface(color = Color(0xFFF7F7F7)) {
            FlashCardReviewBar(
                knowCount     = 13,
                dontKnowCount = 7,
                onKnow        = {},
                onDontKnow    = {}
            )
        }
    }
}

@Preview(name = "ReviewBar — start (all zero)", showBackground = true)
@Composable
private fun PreviewReviewBarStart() {
    MaterialTheme {
        Surface(color = Color(0xFFF7F7F7)) {
            FlashCardReviewBar(
                knowCount     = 0,
                dontKnowCount = 0,
                onKnow        = {},
                onDontKnow    = {}
            )
        }
    }
}

@Preview(name = "ReviewBar — end (all known)", showBackground = true)
@Composable
private fun PreviewReviewBarEnd() {
    MaterialTheme {
        Surface(color = Color(0xFFF7F7F7)) {
            FlashCardReviewBar(
                knowCount     = 20,
                dontKnowCount = 0,
                onKnow        = {},
                onDontKnow    = {}
            )
        }
    }
}

@Preview(name = "ReviewBar — interactive demo", showBackground = true)
@Composable
private fun PreviewReviewBarInteractive() {
    var know     by remember { mutableIntStateOf(13) }
    var dontKnow by remember { mutableIntStateOf(7) }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color    = Color(0xFFF7F7F7)
        ) {
            Column(
                modifier            = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom
            ) {
                FlashCardReviewBar(
                    knowCount     = know,
                    dontKnowCount = dontKnow,
                    onKnow        = { know++ },
                    onDontKnow    = { dontKnow++ },
                    modifier      = Modifier.padding(bottom = 32.dp)
                )
            }
        }
    }
}