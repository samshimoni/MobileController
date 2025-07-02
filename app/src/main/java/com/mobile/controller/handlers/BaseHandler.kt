package com.mobile.controller.handlers

import com.mobile.controller.api.ApiRequest
import com.mobile.controller.api.ApiResponse

interface ApiHandler {
    val path: String
    fun handle(request: ApiRequest): ApiResponse
}