package com.mobile.controller.handlers

import android.content.Context

import android.util.Base64
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
            val errorJson = """{"status":"error","message":"Permission Denied"}"""
            return TakePhotoResponse(
                status = 403,
                contentType = "application/json",
                body = errorJson
            )
        }

        val latch = CountDownLatch(1)
        var base64Image: String? = null

        capturePhotoOnceAndRelease { success, jpegBytes ->
            if (success && jpegBytes != null) {
                base64Image = Base64.encodeToString(jpegBytes, Base64.NO_WRAP)
            }
            latch.countDown()
        }

        latch.await()

        return if (base64Image != null) {
            val jsonResponse = """{"status":"success","image_base64":"$base64Image"}"""
            TakePhotoResponse(
                status = 200,
                contentType = "application/json",
                body = jsonResponse
            )
        } else {
            val errorJson = """{"status":"error","message":"Failed to capture photo"}"""
            TakePhotoResponse(
                status = 500,
                contentType = "application/json",
                body = errorJson
            )
        }
    }

    private fun capturePhotoOnceAndRelease(onResult: (Boolean, ByteArray?) -> Unit) {
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
                onResult(false, null)
                return@withCameraProvider
            }

            takePhotoInMemory(cameraProvider, imageCapture, onResult)
        }
    }

    private fun takePhotoInMemory(
        cameraProvider: ProcessCameraProvider,
        imageCapture: ImageCapture,
        onResult: (Boolean, ByteArray?) -> Unit
    ) {
        val tempFile = File.createTempFile("photo_", ".jpg", context.cacheDir)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(tempFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val bytes = tempFile.readBytes()
                    tempFile.delete()
                    cameraProvider.unbindAll()
                    onResult(true, bytes)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraX", "Photo capture failed: ${exception.message}", exception)
                    tempFile.delete()
                    cameraProvider.unbindAll()
                    onResult(false, null)
                }
            }
        )
    }
}