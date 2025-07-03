package com.mobile.controller.requests

import com.mobile.controller.api.ApiRequest
import com.mobile.controller.api.ApiResponse
import kotlinx.serialization.Serializable

@Serializable
data class TakePhotoRequest(
    val path: String
) : ApiRequest {
    override val uri: String = "/api/take_photo"
}

data class TakePhotoResponse(
    override val status: Int = 200,
    override val contentType: String = "application/json",
    override val body: String
) : ApiResponse