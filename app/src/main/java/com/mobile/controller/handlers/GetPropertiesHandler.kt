package com.mobile.controller.handlers

import com.mobile.controller.api.ApiRequest
import com.mobile.controller.api.ApiResponse
import com.mobile.controller.api.GetPropertiesResponse

class GetPropertiesHandler : ApiHandler {
    override val path: String = "/get_properties"

    override fun handle(request: ApiRequest): ApiResponse {
        return GetPropertiesResponse(code = 200, body = " { result: this is all the properties } ")
    }


}
