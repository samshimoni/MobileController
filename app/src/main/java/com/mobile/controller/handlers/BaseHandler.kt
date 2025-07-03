package com.mobile.controller.handlers

import com.mobile.controller.api.ApiRequest
import com.mobile.controller.api.ApiResponse

interface ApiHandler<T : ApiRequest> {
    val method: String
    val path: String
    fun handle(request: T): ApiResponse
    fun parseRequest(raw: ApiRequest): T

    fun dispatch(raw: ApiRequest): ApiResponse {
        val typed = parseRequest(raw)
        return handle(typed)
    }
}
