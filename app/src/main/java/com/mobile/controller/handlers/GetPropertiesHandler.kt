package com.mobile.controller.handlers

import com.mobile.controller.api.ApiRequest
import com.mobile.controller.api.ApiResponse
import com.mobile.controller.api.GetPropertiesResponse

class GetPropertiesHandler : ApiHandler {
    override val path: String = "/get_properties"

    override fun handle(request: ApiRequest): ApiResponse {
        val res = getProperties();
        return GetPropertiesResponse(res);
    }

    private  fun getProperties(): String {
        return """{"device":"Pixel 7","version":"13","serial":"XYZ123"}""";
    }
}
