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
import kotlinx.serialization.json.Json
import java.io.File

class TakePhotoHandler(
    context: Context,
    lifecycleOwner: LifecycleOwner
) : BaseCameraHandler<TakePhotoRequest, TakePhotoResponse>(context, lifecycleOwner) {

    override val path: String = "/api/take_photo"

    override fun createRequest(method: String, params: Map<String, String>, body: String): TakePhotoRequest {
        if (method != "POST") {
            throw IllegalArgumentException("TakePhotoHandler only supports POST method")
        }
        val json = Json { ignoreUnknownKeys = true }
        return json.decodeFromString(TakePhotoRequest.serializer(), body)
    }

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

    private fun takePhoto(
        cameraProvider: ProcessCameraProvider,
        imageCapture: ImageCapture,
        savePath: String
    ) {
        val photoFile = File(savePath)

        photoFile.parentFile?.mkdirs()

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        Log.i("CameraX", "Calling takePicture")
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