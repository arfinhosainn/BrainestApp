package com.scelio.brainest.presentation.scan

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File

@Composable
actual fun CameraScreen(
    modifier: Modifier,
    captureTrigger: Int,
    onImageCaptured: (String) -> Unit,
    onCameraReady: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember(context) { ContextCompat.getMainExecutor(context) }
    val currentOnImageCaptured by rememberUpdatedState(onImageCaptured)
    val currentOnCameraReady by rememberUpdatedState(onCameraReady)
    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        currentOnCameraReady(granted)
        statusMessage = if (granted) null else "Camera permission denied."
    }

    fun capturePhoto() {
        val captureUseCase = imageCapture
        if (captureUseCase == null) {
            statusMessage = "Camera is still starting. Try again."
            return
        }
        val outputFile = File(context.cacheDir, "scan_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()
        captureUseCase.takePicture(
            outputOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                @Suppress("UNUSED_PARAMETER")
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    statusMessage = "Image captured."
                    currentOnImageCaptured(outputFile.absolutePath)
                }

                override fun onError(exception: ImageCaptureException) {
                    statusMessage = "Failed to capture image: ${exception.message ?: "Unknown error"}"
                    Log.e("CameraScreen", "Image capture failed", exception)
                }
            }
        )
    }

    DisposableEffect(hasPermission, lifecycleOwner) {
        if (!hasPermission) {
            imageCapture = null
            currentOnCameraReady(false)
            onDispose { }
        } else {
            val providerFuture = ProcessCameraProvider.getInstance(context)
            val listener = Runnable {
                val provider = runCatching { providerFuture.get() }.getOrNull()
                if (provider == null) {
                    imageCapture = null
                    currentOnCameraReady(false)
                    statusMessage = "Unable to initialize camera."
                    return@Runnable
                }

                val preview = Preview.Builder().build().also { useCase ->
                    useCase.surfaceProvider = previewView.surfaceProvider
                }
                val captureUseCase = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()
                try {
                    provider.unbindAll()
                    provider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        captureUseCase
                    )
                    imageCapture = captureUseCase
                    statusMessage = null
                    currentOnCameraReady(true)
                } catch (t: Throwable) {
                    imageCapture = null
                    currentOnCameraReady(false)
                    statusMessage = "Unable to start camera preview."
                    Log.e("CameraScreen", "Camera bind failed", t)
                }
            }
            providerFuture.addListener(listener, cameraExecutor)

            onDispose {
                runCatching {
                    providerFuture.get().unbindAll()
                }
                imageCapture = null
                currentOnCameraReady(false)
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            statusMessage = "Camera permission is required."
            permissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            statusMessage = null
        }
        currentOnCameraReady(hasPermission)
    }

    LaunchedEffect(captureTrigger) {
        if (captureTrigger > 0) {
            if (hasPermission) {
                capturePhoto()
            } else {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (hasPermission) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { previewView }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(120.dp))
                Text(
                    text = "Camera permission is required",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                    Text("Grant Permission")
                }
            }
        }

        if (statusMessage != null) {
            Text(
                text = statusMessage ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp)
            )
        }

        CameraCaptureButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            enabled = hasPermission
        ) {
            if (hasPermission) {
                capturePhoto()
            } else {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
}
