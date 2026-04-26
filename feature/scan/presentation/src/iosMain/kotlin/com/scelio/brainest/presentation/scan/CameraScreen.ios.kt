@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.scelio.brainest.presentation.scan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.unit.dp
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCapturePhoto
import platform.AVFoundation.AVCapturePhotoCaptureDelegateProtocol
import platform.AVFoundation.AVCapturePhotoOutput
import platform.AVFoundation.AVCapturePhotoSettings
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.fileDataRepresentation
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSUUID
import platform.Foundation.NSData
import platform.UIKit.UIView
import platform.darwin.NSObject

@Composable
actual fun CameraScreen(
    modifier: Modifier,
    captureTrigger: Int,
    onImageCaptured: (String) -> Unit,
    onCameraReady: (Boolean) -> Unit
) {
    val currentOnImageCaptured by rememberUpdatedState(onImageCaptured)
    val currentOnCameraReady by rememberUpdatedState(onCameraReady)
    var statusMessage by remember { mutableStateOf<String?>(null) }

    val cameraController = remember {
        IOSCameraController(
            onImageCaptured = { path ->
                statusMessage = "Image captured."
                currentOnImageCaptured(path)
            },
            onCameraReady = { ready ->
                currentOnCameraReady(ready)
            },
            onStatus = { message ->
                statusMessage = message
            }
        )
    }

    DisposableEffect(Unit) {
        cameraController.start()
        onDispose {
            cameraController.stop()
        }
    }

    LaunchedEffect(captureTrigger) {
        if (captureTrigger > 0) {
            cameraController.capture()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        UIKitView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                UIView().also { view ->
                    cameraController.attachPreviewTo(view)
                }
            },
            update = { view ->
                cameraController.attachPreviewTo(view)
            }
        )

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
                .padding(bottom = 32.dp)
        ) {
            cameraController.capture()
        }
    }
}

private class IOSCameraController(
    private val onImageCaptured: (String) -> Unit,
    private val onCameraReady: (Boolean) -> Unit,
    private val onStatus: (String?) -> Unit
) {
    private val captureSession = AVCaptureSession()
    private val photoOutput = AVCapturePhotoOutput()
    private var previewLayer: AVCaptureVideoPreviewLayer? = null
    private var isConfigured = false
    private var photoDelegate: PhotoCaptureDelegate? = null

    fun attachPreviewTo(view: UIView) {
        val layer = previewLayer ?: AVCaptureVideoPreviewLayer(session = captureSession).also { createdLayer ->
            createdLayer.videoGravity = AVLayerVideoGravityResizeAspectFill
            previewLayer = createdLayer
            view.layer.addSublayer(createdLayer)
        }
        if (layer.superlayer == null) {
            view.layer.addSublayer(layer)
        }
        layer.frame = view.bounds
    }

    fun start() {
        configureAndStart()
    }

    fun stop() {
        if (captureSession.running) {
            captureSession.stopRunning()
        }
        onCameraReady(false)
    }

    fun capture() {
        if (!captureSession.running) {
            onStatus("Camera not ready.")
            return
        }
        val delegate = PhotoCaptureDelegate(
            onImageCaptured = { path -> onImageCaptured(path) },
            onFailure = { message -> onStatus(message) },
            onCompleted = { photoDelegate = null }
        )
        photoDelegate = delegate
        val settings = AVCapturePhotoSettings.photoSettings()
        photoOutput.capturePhotoWithSettings(settings = settings, delegate = delegate)
    }

    private fun configureAndStart() {
        if (!isConfigured) {
            val cameraDevice = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo)
            if (cameraDevice == null) {
                onCameraReady(false)
                onStatus("No camera available.")
                return
            }
            val input = AVCaptureDeviceInput.deviceInputWithDevice(device = cameraDevice, error = null) as? AVCaptureDeviceInput
            if (input == null || !captureSession.canAddInput(input)) {
                onCameraReady(false)
                onStatus("Unable to access camera input. Check camera permission in Settings.")
                return
            }
            if (!captureSession.canAddOutput(photoOutput)) {
                onCameraReady(false)
                onStatus("Unable to access camera output.")
                return
            }
            captureSession.addInput(input)
            captureSession.addOutput(photoOutput)
            isConfigured = true
        }

        if (!captureSession.running) {
            captureSession.startRunning()
        }
        onStatus(null)
        onCameraReady(true)
    }
}

private class PhotoCaptureDelegate(
    private val onImageCaptured: (String) -> Unit,
    private val onFailure: (String) -> Unit,
    private val onCompleted: () -> Unit
) : NSObject(), AVCapturePhotoCaptureDelegateProtocol {

    @Suppress("UNUSED_PARAMETER")
    override fun captureOutput(
        output: AVCapturePhotoOutput,
        didFinishProcessingPhoto: AVCapturePhoto,
        error: NSError?
    ) {
        if (error != null) {
            onFailure(error.localizedDescription ?: "Capture failed.")
            onCompleted()
            return
        }
        val photoData = didFinishProcessingPhoto.fileDataRepresentation()
        if (photoData == null) {
            onFailure("Could not read captured photo.")
            onCompleted()
            return
        }
        savePhoto(photoData)
        onCompleted()
    }

    private fun savePhoto(photoData: NSData) {
        val filePath = "${NSTemporaryDirectory()}scan_${NSUUID().UUIDString}.jpg"
        val didWrite = NSFileManager.defaultManager.createFileAtPath(
            path = filePath,
            contents = photoData,
            attributes = null
        )
        if (didWrite) {
            onImageCaptured(filePath)
        } else {
            onFailure("Could not save captured photo.")
        }
    }
}
