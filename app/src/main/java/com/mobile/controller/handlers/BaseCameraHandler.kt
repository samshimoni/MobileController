package com.mobile.controller.handlers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.mobile.controller.api.ApiHandler
import com.mobile.controller.api.ApiRequest
import com.mobile.controller.api.ApiResponse

abstract class BaseCameraHandler<Req : ApiRequest, Res : ApiResponse>(
    protected val context: Context,
    protected val lifecycleOwner: LifecycleOwner
) : ApiHandler<Req, Res> {

    protected fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    protected fun withCameraProvider(onReady: (ProcessCameraProvider) -> Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                onReady(cameraProvider)
            } catch (e: Exception) {
                Log.e("CameraX", "Error retrieving camera provider", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }
}