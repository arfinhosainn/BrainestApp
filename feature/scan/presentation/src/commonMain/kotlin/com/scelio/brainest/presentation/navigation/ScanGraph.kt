package com.scelio.brainest.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.scelio.brainest.presentation.scan.CameraScreen
import kotlinx.serialization.Serializable

sealed interface ScanGraphRoutes {
    @Serializable
    data object Graph : ScanGraphRoutes
    @Serializable
    data object Scan : ScanGraphRoutes

    companion object {
        const val SCAN_ROUTE = "scan"
    }
}

fun NavGraphBuilder.scanGraph(
    navController: NavHostController
) {
    composable<ScanGraphRoutes.Scan> { _ ->
        CameraScreen(
            onImageCaptured = { /* Handle captured image */ },
            onCameraReady = { /* Handle camera ready */ },
            onCloseRequested = {
                navController.popBackStack()
            }
        )
    }
}
