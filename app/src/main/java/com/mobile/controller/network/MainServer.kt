package com.mobile.controller.network


import fi.iki.elonen.NanoHTTPD

class WebServer(port: Int, private val router: ApiRouter) : NanoHTTPD(port) {

    override fun serve(session: IHTTPSession): Response {
        val contentLength = session.headers["content-length"]?.toIntOrNull() ?: 0
        val body = if (contentLength > 0) {
            val buffer = ByteArray(contentLength)
            session.inputStream.read(buffer, 0, contentLength)
            String(buffer)
        } else {
            ""
        }

        val params = session.parameters.mapValues { it.value.firstOrNull() ?: "" }
        val method = session.method.name
        val uri = session.uri

        val response = router.route(uri, method, params, body)

        return newFixedLengthResponse(
            Response.Status.lookup(response.status) ?: Response.Status.OK,
            response.contentType,
            response.body
        )
    }
}





