@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.scelio.brainest.presentation.scan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCapturePhoto
import platform.AVFoundation.AVCapturePhotoCaptureDelegateProtocol
import platform.AVFoundation.AVCapturePhotoOutput
import platform.AVFoundation.AVCapturePhotoSettings
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureTorchModeOff
import platform.AVFoundation.AVCaptureTorchModeOn
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
    onCameraReady: (Boolean) -> Unit,
    onCloseRequested: () -> Unit
) {
    val currentOnImageCaptured by rememberUpdatedState(onImageCaptured)
    val currentOnCameraReady by rememberUpdatedState(onCameraReady)
    val currentOnCloseRequested by rememberUpdatedState(onCloseRequested)
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isFlashOn by remember { mutableStateOf(false) }
    var isFlashAvailable by remember { mutableStateOf(false) }

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
            },
            onTorchStateChanged = { enabled ->
                isFlashOn = enabled
            },
            onTorchAvailabilityChanged = { available ->
                isFlashAvailable = available
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

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val previewBottomPadding = CameraPreviewBottomPadding + (maxHeight * CameraPreviewBottomShrinkRatio)

        CameraBottomControls(
            modifier = Modifier.align(Alignment.BottomCenter),
            onGalleryClick = {
                statusMessage = "Gallery will be added next."
            },
            onCaptureClick = {
                cameraController.capture()
            },
            onTypeClick = {
                statusMessage = "Type mode will be added next."
            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = previewBottomPadding)
                .clip(
                    RoundedCornerShape(
                        bottomStart = CameraPreviewBottomCornerRadius,
                        bottomEnd = CameraPreviewBottomCornerRadius
                    )
                )
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
            CameraSquareOverlay(modifier = Modifier.fillMaxSize())
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

        CameraTopControls(
            modifier = Modifier.align(Alignment.TopCenter),
            isFlashOn = isFlashOn,
            isFlashAvailable = isFlashAvailable,
            onCloseClick = { currentOnCloseRequested() },
            onFlashClick = { cameraController.toggleFlashlight() }
        )
    }
}

private class IOSCameraController(
    private val onImageCaptured: (String) -> Unit,
    private val onCameraReady: (Boolean) -> Unit,
    private val onStatus: (String?) -> Unit,
    private val onTorchStateChanged: (Boolean) -> Unit,
    private val onTorchAvailabilityChanged: (Boolean) -> Unit
) {
    private val captureSession = AVCaptureSession()
    private val photoOutput = AVCapturePhotoOutput()
    private var captureDevice: AVCaptureDevice? = null
    private var previewLayer: AVCaptureVideoPreviewLayer? = null
    private var isConfigured = false
    private var isTorchOn = false
    private var isTorchAvailable = false
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
        setTorchEnabled(false)
        if (captureSession.running) {
            captureSession.stopRunning()
        }
        isTorchOn = false
        onTorchStateChanged(false)
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

    fun toggleFlashlight() {
        if (!isTorchAvailable) {
            onStatus("Flash is not available.")
            return
        }
        val target = !isTorchOn
        if (setTorchEnabled(target)) {
            isTorchOn = target
            onTorchStateChanged(target)
        } else {
            onStatus("Unable to toggle flash.")
        }
    }

    private fun configureAndStart() {
        if (!isConfigured) {
            val cameraDevice = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo)
            if (cameraDevice == null) {
                onCameraReady(false)
                onStatus("No camera available.")
                return
            }
            captureDevice = cameraDevice
            isTorchAvailable = runCatching { cameraDevice.hasTorch }.getOrDefault(false)
            onTorchAvailabilityChanged(isTorchAvailable)
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

    private fun setTorchEnabled(enabled: Boolean): Boolean {
        val device = captureDevice ?: return false
        val didLock = runCatching { device.lockForConfiguration(null) }.getOrDefault(false)
        if (!didLock) return false
        return try {
            device.torchMode = if (enabled) AVCaptureTorchModeOn else AVCaptureTorchModeOff
            true
        } catch (error: Throwable) {
            false
        } finally {
            runCatching { device.unlockForConfiguration() }
        }
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
