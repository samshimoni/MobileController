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
import com.mobile.controller.api.ApiRequest
import com.mobile.controller.api.TakePhotoRequest
import com.mobile.controller.api.TakePhotoResponse
import java.io.File

class TakePhotoHandler(
    context: Context,
    lifecycleOwner: LifecycleOwner
) : BaseCameraHandler<TakePhotoRequest>(context, lifecycleOwner) {

    override val path: String = "/take_photo"
    override val method = "GET"

    override fun parseRequest(raw: ApiRequest): TakePhotoRequest {
        return TakePhotoRequest(
            uri = raw.uri,
            params = raw.params,
            body = raw.body
        )
    }

    override fun handle(request: TakePhotoRequest): TakePhotoResponse {

        if (!this.validateCameraResourcePermissions()){
            return TakePhotoResponse(
                body = """{ "status": "Error", "message": "Permission Denied"}"""
            )
        }

        this.capturePhotoOnceAndRelease()

        return TakePhotoResponse(
            body = """{ "status": "Success", "message": "Photo took successfully"}"""
        )
    }

    private fun capturePhotoOnceAndRelease() {
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

            takePhoto(cameraProvider, imageCapture)
        }
    }

    private fun takePhoto(
        cameraProvider: ProcessCameraProvider,
        imageCapture: ImageCapture
    ) {
        val photoFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "IMG_${System.currentTimeMillis()}.jpg"
        )
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