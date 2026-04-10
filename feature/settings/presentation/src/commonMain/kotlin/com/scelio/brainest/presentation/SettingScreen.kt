package com.scelio.brainest.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import brainest.feature.settings.presentation.generated.resources.Res
import brainest.feature.settings.presentation.generated.resources.ic_arrow_left
import brainest.feature.settings.presentation.generated.resources.ic_key
import brainest.feature.settings.presentation.generated.resources.ic_language
import brainest.feature.settings.presentation.generated.resources.ic_logout
import brainest.feature.settings.presentation.generated.resources.ic_support
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.extended
import com.scelio.brainest.presentation.components.ProfileCard
import com.scelio.brainest.presentation.components.SettingMenuItem
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SettingScreen(
    name: String,
    joinedText: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onNameChange: (String) -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onSupportClick: () -> Unit = {},
    onLanguageClick: () -> Unit = {},
    onHotlineClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.extended.surfaceLower)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector =  vectorResource(Res.drawable.ic_arrow_left),
                    contentDescription = "Back"
                )
            }

            Text(
                text = "Setting",
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        ProfileCard(
            name = name,
            joinedText = joinedText,
            onNameChange = onNameChange
        )

        Spacer(modifier = Modifier.height(30.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SettingMenuItem(
                title = "Change Password",
                leadingIcon = vectorResource(Res.drawable.ic_key),
                onClick = onChangePasswordClick
            )
            SettingMenuItem(
                title = "Support",
                leadingIcon = vectorResource(Res.drawable.ic_support),
                onClick = onSupportClick
            )
            SettingMenuItem(
                title = "Language",
                leadingIcon = vectorResource(Res.drawable.ic_language),
                onClick = onLanguageClick
            )
            SettingMenuItem(
                title = "Hotline",
                leadingIcon = vectorResource(Res.drawable.ic_support),
                onClick = onHotlineClick
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        SettingMenuItem(
            title = "Logout",
            leadingIcon = vectorResource(Res.drawable.ic_logout),
            onClick = onLogoutClick,
            showTrailingIcon = false,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            borderColor = Color.Transparent
        )
    }
}

@Preview
@Composable
private fun SettingScreenPreview() {
    BrainestTheme {
        var name by rememberSaveable { mutableStateOf("Wdz") }

        Surface(
            color = MaterialTheme.colorScheme.extended.surfaceLower
        ) {
            SettingScreen(
                name = name,
                joinedText = "Joined Oct 2017",
                onNameChange = { name = it }
            )
        }
    }
}
