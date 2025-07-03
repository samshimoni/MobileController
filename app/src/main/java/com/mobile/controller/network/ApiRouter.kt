package com.mobile.controller.network

import com.mobile.controller.api.ApiHandler
import com.mobile.controller.api.ApiRequest
import com.mobile.controller.api.ApiResponse

class ApiRouter(
    private val handlers: List<ApiHandler<*, *>>
) {
    fun route(
        uri: String,
        method: String,
        params: Map<String, String>,
        body: String
    ): ApiResponse {
        val handler = handlers.firstOrNull { it.path == uri }
            ?: return object : ApiResponse {
                override val status: Int = 404
                override val contentType: String = "application/json"
                override val body: String = """{"error":"Not found"}"""
            }
        @Suppress("UNCHECKED_CAST")
        val typedHandler = handler as ApiHandler<ApiRequest, ApiResponse>
        val request = typedHandler.createRequest(method, params, body)
        return typedHandler.handle(request)
    }
}