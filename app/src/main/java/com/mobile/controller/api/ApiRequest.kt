package com.mobile.controller.api

interface ApiRequest {
    val uri: String
}

interface ApiResponse {
    val status: Int
    val contentType: String
    val body: String
}