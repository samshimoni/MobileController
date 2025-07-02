package com.mobile.controller.api

abstract class ApiRequest(
    var method: String,
    var uri: String,
    var params: Map<String, String> = emptyMap(),
    var body: String = ""
)

abstract class ApiResponse(
    var code: Int,
    var contentType: String = "application/json",
    var body: String
)

class BaseRequest(
    method: String,
    uri: String,
    params: Map<String, String> = emptyMap(),
    body: String = ""
) : ApiRequest(method, uri, params, body)

class ErrorResponse(
    code: Int,
    body: String
) : ApiResponse(
    code = code,
    contentType = "application/json",
    body = body
)