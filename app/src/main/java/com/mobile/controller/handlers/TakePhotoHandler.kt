package com.mobile.controller.handlers

import android.content.Context
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

class TakePhotoHandler(
    context: Context,
    lifecycleOwner: LifecycleOwner
) : BaseCameraHandler<TakePhotoRequest, TakePhotoResponse>(context, lifecycleOwner) {

    override val path: String = "/api/take_photo"

    /**
     * Handles a [TakePhotoRequest] by checking camera permission.
     * Returns a 403 response if permission is denied.
     */
    override fun handle(request: TakePhotoRequest): TakePhotoResponse {
        if (!hasCameraPermission()) {
            return TakePhotoResponse(
                status = 403,
                body = """{"status": "error", "message": "Permission Denied"}"""
            )
        }

        capturePhotoOnceAndRelease(request.path)

        return TakePhotoResponse(
            status = 200,
            body = """{"status": "success", "message": "Photo captured at ${request.path}}"""
        )
    }

    /**
     * Captures a single photo and then releases the camera resource.
     *
     * Binds an [ImageCapture] use case, takes a photo to the given [savePath],
     * and unbinds afterward to free the camera.
     *
     * @param savePath the file path where the captured photo will be saved.
     */
    private fun capturePhotoOnceAndRelease(savePath: String) {
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
                return@withCameraProvider
            }

            takePhoto(cameraProvider, imageCapture, savePath)
        }
    }

    /**
     * Captures a photo and saves it to a timestamped file in the specified directory.
     *
     * Creates the directory if it doesn't exist, then takes a picture using the given
     * [ImageCapture] instance. On success or failure, logs the outcome and releases
     * the camera by unbinding all use cases.
     *
     * @param cameraProvider the [ProcessCameraProvider] managing the camera lifecycle.
     * @param imageCapture the [ImageCapture] use case used to take the photo.
     * @param saveDirectory the directory path where the photo file will be saved.
     */
    private fun takePhoto(
        cameraProvider: ProcessCameraProvider,
        imageCapture: ImageCapture,
        saveDirectory: String
    ) {
        val directory = File(saveDirectory)

        if (!directory.exists()) {
            directory.mkdirs()
        }

        val timeStamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(java.util.Date())
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
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraX", "Photo capture failed: ${exception.message}", exception)
                    cameraProvider.unbindAll()
                }
            }
        )
    }
}