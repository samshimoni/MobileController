package com.mobile.controller.api

class GetPropertiesRequest(
    override val method: String,
    override val uri: String,
    override val params: Map<String, String> = emptyMap(),
    override val body: String = ""
) : ApiRequest {

    val details: String
        get() = params["details"] ?: "basic"
}

class GetPropertiesResponse(
    override val code: Int = 200,
    override val contentType: String = "application/json",
    override val body: String
) : ApiResponse

