package com.mobile.controller.network

import com.mobile.controller.api.ApiRouter
import com.mobile.controller.api.GenericRequest
import fi.iki.elonen.NanoHTTPD

class WebServer(port: Int, private val router: ApiRouter) : NanoHTTPD(port) {
    override fun serve(session: IHTTPSession): Response {

        val request = GenericRequest(
            session.uri,
            session.parameters.mapValues {  it.value.firstOrNull() ?: "" },
            "")

        val response = router.route(request)

        return newFixedLengthResponse(
            Response.Status.lookup(response.code) ?: Response.Status.OK,
            response.contentType,
            response.body
        )
    }
}
