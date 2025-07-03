package com.mobile.controller.api

import kotlinx.serialization.json.Json

data class GetPropertiesPayload(
    val details: String,
    val quality: String
)

class GetPropertiesRequest(
    override val uri: String,
    override val params: Map<String, String> = emptyMap(),
    override val body: String
) : ApiRequest {
    val payload: GetPropertiesPayload
        get() = parseBody(body)

    private fun parseBody(body: String): GetPropertiesPayload {
        return Json.decodeFromString(body)
    }
}

class GetPropertiesResponse(
    override val code: Int = 200,
    override val contentType: String = "application/json",
    override val body: String
) : ApiResponse

