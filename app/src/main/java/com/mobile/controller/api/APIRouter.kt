package com.mobile.controller.api

import com.mobile.controller.handlers.ApiHandler

class ApiRouter(
    private val handlers: List<ApiHandler<out ApiRequest>>
) {
    fun route(request: ApiRequest): ApiResponse {
        val handler = handlers.find { it.path == request.uri }
        return handler?.dispatch(request)
            ?: ErrorResponse(404, body = """{"error":"Not found"}""")
    }
}