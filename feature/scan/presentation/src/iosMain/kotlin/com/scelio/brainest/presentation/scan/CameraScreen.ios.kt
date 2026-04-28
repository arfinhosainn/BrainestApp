@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.scelio.brainest.presentation.scan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import kotlinx.coroutines.delay
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
import platform.AVFoundation.hasTorch
import platform.AVFoundation.torchMode
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUUID
import platform.UIKit.UIApplication
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerImageURL
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIView
import platform.UIKit.UIWindow
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
    var galleryPreviewPath by remember { mutableStateOf<String?>(null) }
    var isGalleryScanning by remember { mutableStateOf(false) }
    val galleryPicker = remember {
        IOSGalleryImagePicker(
            onImagePicked = { path ->
                statusMessage = null
                galleryPreviewPath = path
                isGalleryScanning = true
            },
            onError = { message ->
                statusMessage = message
            }
        )
    }

    val cameraController = remember {
        IOSCameraController(
            onImageCaptured = { path ->
                statusMessage = null
                galleryPreviewPath = path
                isGalleryScanning = true
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

    LaunchedEffect(isGalleryScanning, galleryPreviewPath) {
        val selectedPath = galleryPreviewPath
        if (isGalleryScanning && selectedPath != null) {
            delay(1700)
            currentOnImageCaptured(selectedPath)
            isGalleryScanning = false
            galleryPreviewPath = null
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
                galleryPicker.launch()
            },
            onCaptureClick = {
                cameraController.capture()
            },
            onTypeClick = {
                statusMessage = "Type mode will be added next."
            }
        )

        BoxWithConstraints(
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
            if (galleryPreviewPath != null) {
                val circleSize = minOf(maxWidth, maxHeight) * CameraPreviewCutoutRatio
                LocalImagePreview(
                    imagePath = galleryPreviewPath.orEmpty(),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(circleSize)
                        .clip(CircleShape)
                )
            } else {
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
            }
            CameraPreviewOverlay(modifier = Modifier.fillMaxSize())
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

        CameraFullscreenScanOverlay(
            modifier = Modifier.fillMaxSize(),
            isVisible = isGalleryScanning,
            previewBottomInset = previewBottomPadding
        )
        if (isGalleryScanning) {
            CameraScanningIndicator(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = CameraControlsPanelHeight + 24.dp)
            )
        }
    }
}

private class IOSGalleryImagePicker(
    private val onImagePicked: (String) -> Unit,
    private val onError: (String) -> Unit
) {
    private val delegate = GalleryImagePickerDelegate(
        onImagePicked = onImagePicked,
        onError = onError
    )

    fun launch() {
        val rootViewController = findRootViewController()
        if (rootViewController == null) {
            onError("Unable to open image gallery.")
            return
        }

        val picker = UIImagePickerController().apply {
            sourceType =
                UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
            allowsEditing = false
            delegate = this@IOSGalleryImagePicker.delegate
        }

        rootViewController.presentViewController(
            picker,
            animated = true,
            completion = null
        )
    }
}

private class GalleryImagePickerDelegate(
    private val onImagePicked: (String) -> Unit,
    private val onError: (String) -> Unit
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true, completion = null)
    }

    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>
    ) {
        val imageUrl = didFinishPickingMediaWithInfo[UIImagePickerControllerImageURL] as? NSURL
        if (imageUrl == null) {
            onError("Unable to read selected image.")
            picker.dismissViewControllerAnimated(true, completion = null)
            return
        }

        val sourcePath = imageUrl.path
        if (sourcePath == null) {
            onError("Unable to read selected image path.")
            picker.dismissViewControllerAnimated(true, completion = null)
            return
        }

        val data = NSFileManager.defaultManager.contentsAtPath(sourcePath)
        if (data == null) {
            onError("Unable to read selected image.")
            picker.dismissViewControllerAnimated(true, completion = null)
            return
        }

        val filePath = "${NSTemporaryDirectory()}scan_gallery_${NSUUID().UUIDString}.jpg"
        val didWrite = NSFileManager.defaultManager.createFileAtPath(
            path = filePath,
            contents = data,
            attributes = null
        )

        if (didWrite) {
            onImagePicked(filePath)
        } else {
            onError("Unable to save selected image.")
        }

        picker.dismissViewControllerAnimated(true, completion = null)
    }
}

private fun findRootViewController() = UIApplication.sharedApplication.keyWindow?.rootViewController
    ?: UIApplication.sharedApplication.windows
        .filterIsInstance<UIWindow>()
        .firstOrNull { it.isKeyWindow() }
        ?.rootViewController
    ?: UIApplication.sharedApplication.windows
        .filterIsInstance<UIWindow>()
        .firstOrNull()
        ?.rootViewController

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
