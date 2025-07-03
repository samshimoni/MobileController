package com.mobile.controller.handlers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.mobile.controller.api.ApiRequest

abstract class BaseCameraHandler<T : ApiRequest>(
    protected val context: Context,
    protected val lifecycleOwner: LifecycleOwner
) : ApiHandler<T> {

    protected fun validateCameraResourcePermissions(): Boolean {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        return hasPermission
    }

    protected fun withCameraProvider(
        onReady: (ProcessCameraProvider) -> Unit
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                onReady(cameraProvider)
            } catch (e: Exception) {
                Log.e("CameraX", "Failed to get camera provider", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }
}