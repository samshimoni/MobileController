package com.mobile.controller.handlers

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.mobile.controller.api.ApiHandler
import com.mobile.controller.requests.OpenCameraRequest
import com.mobile.controller.requests.OpenCameraResponse

class OpenCameraHandler(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val previewView: PreviewView
) : ApiHandler<OpenCameraRequest, OpenCameraResponse> {

    override val path: String = "/api/open_camera"

    override fun handle(request: OpenCameraRequest): OpenCameraResponse {

        openCameraAndShowPreview()

        return OpenCameraResponse(
            status = 200,
            body = """{"status":"ok","message":"Camera opened successfully"}"""
        )
    }

    private fun openCameraAndShowPreview() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            Log.i("CameraX", "CameraProvider ready (showing preview)")

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview
                )
            } catch (e: Exception) {
                Log.e("CameraX", "Bind failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }
}
