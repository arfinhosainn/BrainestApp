package com.scelio.brainest.designsystem.components.navbar.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import brainest.core.designsystem.generated.resources.Res
import brainest.core.designsystem.generated.resources.happy
import brainest.core.designsystem.generated.resources.profile_picture
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CircularProfilePicture() {
    Image(
        imageVector = vectorResource(Res.drawable.happy),
        contentDescription = stringResource(Res.string.profile_picture),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .border(
                width = 4.dp,
                color = Color.White,
                shape = CircleShape
            )
    )
}

@Preview()
@Composable
fun DefaultPreview() {
    CircularProfilePicture()
}
