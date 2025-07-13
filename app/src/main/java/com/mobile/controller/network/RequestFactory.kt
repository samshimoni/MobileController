package com.mobile.controller.network

import com.mobile.controller.api.ApiRequest
import com.mobile.controller.requests.GetPropertiesRequest
import com.mobile.controller.requests.OpenCameraRequest
import com.mobile.controller.requests.TakePhotoRequest
import kotlinx.serialization.json.Json

class RequestFactory {

    private val creators: Map<String, (String, String, Map<String, String>, String) -> ApiRequest> = mapOf(
        "/api/get_properties" to { uri, method, params, body ->
            GetPropertiesRequest(uri)
        },
        "/api/open_camera" to { uri, method, params, body ->
            OpenCameraRequest(uri)
        },
        "/api/take_photo" to { uri, method, params, body ->
            TakePhotoRequest(uri)
        }
    )

    /*
    createRequest:Get the rest api request and return the suitable ApiRequest

    Parameters:
    uri: The uri.
    method: method GET/POST.
    params: GET parameters.
    body: POST parameters.

    Returns:
    ApiResponse: The suitable ApiResponse.
    */

    fun createRequest(
        uri: String,
        method: String,
        params: Map<String, String>,
        body: String
    ): ApiRequest {
        val creator = creators[uri]
            ?: throw IllegalArgumentException("Unknown URI: $uri")
        return creator(uri, method, params, body)
    }
}
