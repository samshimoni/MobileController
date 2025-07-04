package com.mobile.controller.handlers

import com.mobile.controller.api.ApiHandler
import com.mobile.controller.requests.GetPropertiesRequest
import com.mobile.controller.requests.GetPropertiesResponse

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class GetPropertiesHandler : ApiHandler<GetPropertiesRequest, GetPropertiesResponse> {
    override val path: String = "/api/get_properties"

    override fun handle(request: GetPropertiesRequest): GetPropertiesResponse {
        val deviceInfo = getDeviceInfo()
        return GetPropertiesResponse(body = Json.encodeToString(deviceInfo))
    }

    @Serializable
    private data class DeviceInfo(
        val androidVersion: String,
        val sdkInt: Int,
        val model: String,
        val manufacturer: String,
        val fingerprint: String,
        val abis: String
    )

        private fun getDeviceInfo() : DeviceInfo {
        return DeviceInfo(
            androidVersion = android.os.Build.VERSION.RELEASE ?: "unknown",
            sdkInt = android.os.Build.VERSION.SDK_INT,
            model = android.os.Build.MODEL,
            manufacturer = android.os.Build.MANUFACTURER,
            fingerprint = android.os.Build.FINGERPRINT,
            abis = android.os.Build.SUPPORTED_ABIS.joinToString(",")
        )
    }
}