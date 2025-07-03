package com.mobile.controller.handlers

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.mobile.controller.requests.OpenCameraRequest
import com.mobile.controller.requests.OpenCameraResponse

class OpenCameraHandler(
    context: Context,
    lifecycleOwner: LifecycleOwner
) : BaseCameraHandler<OpenCameraRequest, OpenCameraResponse>(context, lifecycleOwner) {

    override val path: String = "/api/open_camera"

    override fun createRequest(method: String, params: Map<String, String>, body: String): OpenCameraRequest {
        return OpenCameraRequest( uri = path)
    }

    override fun handle(request: OpenCameraRequest): OpenCameraResponse {
        if (!hasCameraPermission()) {
            return OpenCameraResponse(
                status = 403,
                body = """{"status":"error","message":"Camera permission denied"}"""
            )
        }

        openCameraAndHoldResource()

        return OpenCameraResponse(
            status = 200,
            body = """{"status":"ok","message":"Camera opened successfully"}"""
        )
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