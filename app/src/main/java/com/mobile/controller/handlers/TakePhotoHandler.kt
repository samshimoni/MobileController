package com.mobile.controller.handlers

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.mobile.controller.requests.TakePhotoRequest
import com.mobile.controller.requests.TakePhotoResponse
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CountDownLatch

class TakePhotoHandler(
    context: Context,
    lifecycleOwner: LifecycleOwner
) : BaseCameraHandler<TakePhotoRequest, TakePhotoResponse>(context, lifecycleOwner) {

    override val path: String = "/api/take_photo"

    /**
     * Handles a [TakePhotoRequest] by checking camera permission.
     * Returns a 403 response if permission is denied.
     * Returns a 500 response if photo capture fails.
     */
    override fun handle(request: TakePhotoRequest): TakePhotoResponse {
        if (!hasCameraPermission()) {
            return TakePhotoResponse(
                status = 403,
                body = """{"status": "error", "message": "Permission Denied"}"""
            )
        }

        val latch = CountDownLatch(1)
        var success = false

        // Safe fallback directory
        val safePath = request.path.ifEmpty {
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath
                ?: context.filesDir.absolutePath
        }

        capturePhotoOnceAndRelease(safePath) { result ->
            success = result
            latch.countDown()
        }

        latch.await()

        return if (success) {
            TakePhotoResponse(
                status = 200,
                body = """{"status": "success", "message": "Photo captured at $safePath"}"""
            )
        } else {
            TakePhotoResponse(
                status = 500,
                body = """{"status": "error", "message": "Failed to save photo"}"""
            )
        }
    }

    /**
     * Captures a single photo and then releases the camera resource.
     *
     * @param savePath the file path where the captured photo will be saved.
     * @param onResult callback with true if success, false if failure.
     */
    private fun capturePhotoOnceAndRelease(savePath: String, onResult: (Boolean) -> Unit) {
        withCameraProvider { cameraProvider ->
            val imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    imageCapture
                )
            } catch (e: Exception) {
                Log.e("CameraX", "Bind failed", e)
                onResult(false)
                return@withCameraProvider
            }

            takePhoto(cameraProvider, imageCapture, savePath, onResult)
        }
    }

    /**
     * Captures a photo and saves it to a timestamped file in the specified directory.
     *
     * @param cameraProvider the [ProcessCameraProvider] managing the camera lifecycle.
     * @param imageCapture the [ImageCapture] use case used to take the photo.
     * @param saveDirectory the directory path where the photo file will be saved.
     * @param onResult callback indicating success or failure.
     */
    private fun takePhoto(
        cameraProvider: ProcessCameraProvider,
        imageCapture: ImageCapture,
        saveDirectory: String,
        onResult: (Boolean) -> Unit
    ) {
        val directory = File(saveDirectory)

        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                Log.e("CameraX", "Failed to create directory: $saveDirectory")
                onResult(false)
                return
            }
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "photo_$timeStamp.jpg"
        val photoFile = File(directory, fileName)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        Log.i("CameraX", "Calling takePicture to ${photoFile.absolutePath}")

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Log.i("CameraX", "Photo saved: ${photoFile.absolutePath}")
                    cameraProvider.unbindAll()
                    onResult(true)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraX", "Photo capture failed: ${exception.message}", exception)
                    cameraProvider.unbindAll()
                    onResult(false)
                }
            }
        )
    }
}
