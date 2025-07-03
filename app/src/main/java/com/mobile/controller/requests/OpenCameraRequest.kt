package com.mobile.controller.requests

import com.mobile.controller.api.ApiRequest
import com.mobile.controller.api.ApiResponse
import kotlinx.serialization.Serializable

@Serializable
data class OpenCameraRequest(
    override val uri: String,
) : ApiRequest


data class OpenCameraResponse(
    override val status: Int = 200,
    override val contentType: String = "application/json",
    override val body: String
) : ApiResponse