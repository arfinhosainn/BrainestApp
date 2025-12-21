package com.scelio.brainest.presentation.chat_detail.components


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.extended
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailHeader(
    scrollState: LazyListState,
    onCloseClick: () -> Unit,
) {
    val scrollProgress = remember(scrollState.firstVisibleItemScrollOffset) {
        min(1f, scrollState.firstVisibleItemScrollOffset / 300f)
    }

    val headerColor by animateColorAsState(
        targetValue = lerp(
            start = MaterialTheme.colorScheme.background,
            stop = Color.White,     // Scrolled color
            fraction = scrollProgress
        ),
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )

    val elevation by animateDpAsState(
        targetValue = if (scrollProgress > 0.1f) 4.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )

    TopAppBar(
        title = {
            Text(
                text = "Ask Anything",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Serif
            )
        },
        actions = {
            IconButton(
                onClick = onCloseClick,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(32.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.extended.textSecondary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = headerColor, // Dynamic color
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = if (isSystemInDarkTheme()) Color.White else Color.Black
        )
    )
}


@Preview
@Composable
fun PreviewChatListHeader() {
    BrainestTheme(darkTheme = true) {
        ChatDetailHeader(scrollState = remember { LazyListState() }, onCloseClick = {})
    }

}