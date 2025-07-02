package com.mobile.controller.api

import com.mobile.controller.handlers.ApiHandler


class ApiRouter(private val handlers: List<ApiHandler>) {
    fun route(request: ApiRequest): ApiResponse {
        val handler = handlers.find { it.path == request.uri }
        return handler?.handle(request)
            ?: ErrorResponse(404, body = """{"error":"Not found"}""")
    }
}