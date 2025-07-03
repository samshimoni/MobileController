package com.mobile.controller.handlers

import com.mobile.controller.api.ApiRequest
import com.mobile.controller.api.ApiResponse
import com.mobile.controller.api.GetPropertiesResponse
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class GetPropertiesHandler : ApiHandler {

    @Serializable
    private data class DeviceInfo(
        val androidVersion: String,
        val sdkInt: Int,
        val model: String,
        val manufacturer: String,
        val fingerprint: String,
        val abis: String
    )

    override val path: String = "/get_properties"
    override val method = "GET"

    override fun handle(request: ApiRequest): ApiResponse {
        val info = this.getDeviceInfo();

        val json = Json { prettyPrint = true }.encodeToString(info)

        return GetPropertiesResponse(code = 200, body = json)
    }

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