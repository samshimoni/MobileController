package com.mobile.controller.network

import com.mobile.controller.api.ApiRouter
import com.mobile.controller.api.GenericRequest
import fi.iki.elonen.NanoHTTPD

class WebServer(port: Int, private val router: ApiRouter) : NanoHTTPD(port) {
    override fun serve(session: IHTTPSession): Response {
        val map = HashMap<String, String>()
        try {
            session.parseBody(map)
        } catch (e: Exception) {
            return newFixedLengthResponse(
                Response.Status.INTERNAL_ERROR,
                "text/plain",
                "Failed to parse request body: ${e.message}"
            )
        }

        val body = map["postData"] ?: ""

        // Parse query parameters (GET-style) if present
        val params = session.parameters.mapValues { it.value.firstOrNull() ?: "" }

        val request = GenericRequest(
            session.uri,
            params,
            body
        )

        val response = router.route(request)

        return newFixedLengthResponse(
            Response.Status.lookup(response.code) ?: Response.Status.OK,
            response.contentType,
            response.body
        )
    }
}




