package com.brainest.presentation.introduction.permission

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scelio.brainest.designsystem.BrainestTheme
import com.scelio.brainest.designsystem.BricolageGrotesq
import com.scelio.brainest.designsystem.Typography
import com.scelio.brainest.designsystem.components.buttons.BrainestButton
import com.scelio.brainest.presentation.permission.Permission
import com.scelio.brainest.presentation.permission.PermissionState
import com.scelio.brainest.presentation.permission.rememberPermissionController
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun PermissionScreenOnboarding(
    onAllowClick: () -> Unit = {},
    onContinue: () -> Unit = {}
) {
    val permissionController = rememberPermissionController()
    val scope = rememberCoroutineScope()

    val permissionStates = remember {
        mutableStateMapOf(
            Permission.CAMERA to PermissionState.NOT_DETERMINED,
            Permission.MICROPHONE to PermissionState.NOT_DETERMINED,
            Permission.NOTIFICATIONS to PermissionState.NOT_DETERMINED,
        )
    }

    val permissionsToRequest = listOf(
        Permission.CAMERA,
        Permission.MICROPHONE,
        Permission.NOTIFICATIONS
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F0EB))
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Enable Permission",
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

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "To get the best experience, we'd like access to:",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = Color(0xFF666666),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                PermissionItem(
                    modifier = Modifier.padding(20.dp),
                    icon = Icons.Outlined.Menu,
                    title = "Camera",
                    description = "Snap photos of your math problems to solve them instantly.",
                    state = permissionStates[Permission.CAMERA] ?: PermissionState.NOT_DETERMINED
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                PermissionItem(
                    modifier = Modifier.padding(20.dp),
                    icon = Icons.Outlined.Create,
                    title = "Microphone",
                    description = "Use voice commands to ask questions and get help.",
                    state = permissionStates[Permission.MICROPHONE] ?: PermissionState.NOT_DETERMINED
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                PermissionItem(
                    modifier = Modifier.padding(20.dp),
                    icon = Icons.Outlined.Notifications,
                    title = "Notifications",
                    description = "Stay updated with reminders and learning alerts.",
                    state = permissionStates[Permission.NOTIFICATIONS] ?: PermissionState.NOT_DETERMINED
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            BrainestButton(
                text = "Allow Permissions",
                onClick = {
                    scope.launch {
                        permissionsToRequest.forEach { permission ->
                            val result = permissionController.requestPermission(permission)
                            permissionStates[permission] = result
                        }
                        onAllowClick()
                    }
                },
                enabled = true,
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

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF666666)
                )
            ) {
                Text(
                    text = "Skip for now",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PermissionScreenOnboardingPreview() {
    BrainestTheme {
        PermissionScreenOnboarding(
            onAllowClick = {},
            onContinue = {}
        )
    }
}
