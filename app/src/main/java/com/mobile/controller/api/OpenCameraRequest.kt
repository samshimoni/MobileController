package com.mobile.controller.api

class OpenCameraRequest(
    override val uri: String,
    override val params: Map<String, String> = emptyMap(),
    override val body: String = ""
) : ApiRequest {

}

class OpenCameraResponse(
    override val code: Int = 200,
    override val contentType: String = "application/json",
    override val body: String
) : ApiResponse{

}


