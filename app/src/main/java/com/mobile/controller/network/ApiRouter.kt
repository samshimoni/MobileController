package com.mobile.controller.network

import com.mobile.controller.api.ApiHandler
import com.mobile.controller.api.ApiRequest
import com.mobile.controller.api.ApiResponse

class ApiRouter(
    private val handlers: List<ApiHandler<*, *>>,
    private var requestFactory : RequestFactory
) {
    /**
     * Routes an API request to the appropriate handler based on the URI.
     *
     * Finds the first handler matching the given [uri], creates a request using
     * the provided parameters, and invokes the handler's `handle` method.
     * Returns a 404 response if no matching handler is found.
     *
     * @param uri the request URI.
     * @param method the HTTP method (e.g., GET, POST).
     * @param params map of query or path parameters.
     * @param body the request body as a string.
     * @return the response from the matched handler or a 404 error response.
     */

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

        val request = requestFactory.createRequest(uri, method, params, body)

        @Suppress("UNCHECKED_CAST")
        val typedHandler = handler as ApiHandler<ApiRequest, ApiResponse>
        return typedHandler.handle(request)
    }
}