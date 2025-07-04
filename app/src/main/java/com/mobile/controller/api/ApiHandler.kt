package com.mobile.controller.api

interface ApiHandler<Req : ApiRequest, Res : ApiResponse> {
    val path: String
    fun handle(request: Req): Res
}