package com.scelio.brainest.presentation.chat_list.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import brainest.feature.chat.presentation.generated.resources.Res
import brainest.feature.chat.presentation.generated.resources.empty_chat
import brainest.feature.chat.presentation.generated.resources.no_messages
import brainest.feature.chat.presentation.generated.resources.no_messages_subtitle
import com.scelio.brainest.designsystem.extended
import com.scelio.brainest.presentation.util.DeviceConfiguration
import com.scelio.brainest.presentation.util.currentDeviceConfiguration
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EmptyChatSection(
    modifier: Modifier = Modifier
) {
    val configuration = currentDeviceConfiguration()
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(Res.drawable.empty_chat),
            contentDescription = stringResource(Res.string.no_messages),
            modifier = Modifier.size(
                if(configuration == DeviceConfiguration.MOBILE_LANDSCAPE) {
                    125.dp
                } else {
                    200.dp
                }
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(Res.string.no_messages),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.extended.textPrimary
        )
        Text(
            text = stringResource(Res.string.no_messages_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.extended.textSecondary
        )
    }
}

@Preview
@Composable
fun PreviewEmptyChatSection(){
    EmptyChatSection()
}