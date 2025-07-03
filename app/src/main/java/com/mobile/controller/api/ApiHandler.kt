package com.mobile.controller.api

interface ApiHandler<Req : ApiRequest, Res : ApiResponse> {
    val path: String
    fun createRequest(method: String, params: Map<String,String>, body: String): Req
    fun handle(request: Req): Res
}