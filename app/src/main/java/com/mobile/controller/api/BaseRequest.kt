package com.mobile.controller.api

interface ApiRequest {
    val method: String
    val uri: String
    val params: Map<String, String>
    val body: String
}

interface ApiResponse {
    val code: Int
    val contentType: String
    val body: String
}

class GenericRequest(
    override val method: String,
    override val uri: String,
    override val params: Map<String, String> = emptyMap(),
    override val body: String = ""
) : ApiRequest

class ErrorResponse(
    override val code: Int,
    override val body: String,
    override val contentType: String = "application/json"
) : ApiResponse