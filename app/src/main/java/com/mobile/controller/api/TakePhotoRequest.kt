package com.mobile.controller.api

class TakePhotoRequest(
    override val uri: String,
    override val params: Map<String, String> = emptyMap(),
    override val body: String = ""
) : ApiRequest

class TakePhotoResponse(
    override val code: Int = 200,
    override val contentType: String = "application/json",
    override val body: String
) : ApiResponse
