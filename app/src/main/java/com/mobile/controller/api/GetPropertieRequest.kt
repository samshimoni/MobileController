package com.mobile.controller.api

class GetPropertiesRequest(
    method: String,
    uri: String,
    params: Map<String, String> = emptyMap(),
    body: String = ""
) : ApiRequest(method, uri, params, body) {

    val details: String
        get() = params["details"] ?: "basic"
}

class GetPropertiesResponse(body: String) : ApiResponse(
    code = 200,
    contentType = "application/json",
    body = body
)

