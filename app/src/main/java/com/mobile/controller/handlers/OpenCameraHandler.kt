package com.mobile.controller.handlers

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.mobile.controller.api.ApiRequest
import com.mobile.controller.api.OpenCameraRequest
import com.mobile.controller.api.OpenCameraResponse

class OpenCameraHandler(
    context: Context,
    lifecycleOwner: LifecycleOwner
) : BaseCameraHandler<OpenCameraRequest>(context, lifecycleOwner) {

    override val path: String = "/open_camera"
    override val method = "GET"

    override fun parseRequest(raw: ApiRequest): OpenCameraRequest {
        return OpenCameraRequest(
            uri = raw.uri,
            params = raw.params,
            body = raw.body
        )
    }

    private var cameraProvider: ProcessCameraProvider? = null

    override fun handle(request: OpenCameraRequest): OpenCameraResponse {
        if (!this.validateCameraResourcePermissions()){
            return OpenCameraResponse(
                body = """{ "status": "Error", "message": "Permission Denied"}"""
            )
        }

        openCameraAndHoldResource()

        return OpenCameraResponse(body = """{ "status": "Success", "message": "Camera Opened Successfully"}""" )
    }

    private fun openCameraAndHoldResource() {
        withCameraProvider { cameraProvider ->
            Log.i("CameraX", "CameraProvider ready (holding resource)")

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                imageProxy.close()
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    imageAnalysis
                )
            } catch (e: Exception) {
                Log.e("CameraX", "Bind failed", e)
            }
        }
    }
}