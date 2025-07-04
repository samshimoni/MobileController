package com.mobile.controller

import com.mobile.controller.requests.*
import com.mobile.controller.network.RequestFactory
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RequestFactoryTest {

    private val factory = RequestFactory()

    @Test
    fun testGetPropertiesRequest() {
        val request = factory.createRequest(
            uri = "/api/get_properties",
            method = "GET",
            params = emptyMap(),
            body = ""
        )
        assertTrue(request is GetPropertiesRequest)
        assertEquals("/api/get_properties", request.uri)
    }

    @Test
    fun testOpenCameraRequest() {
        val request = factory.createRequest(
            uri = "/api/open_camera",
            method = "GET",
            params = emptyMap(),
            body = ""
        )
        assertTrue(request is OpenCameraRequest)
        assertEquals("/api/open_camera", request.uri)
    }

    @Test
    fun testTakePhotoRequest() {
        val jsonBody = Json.encodeToString(
            TakePhotoRequest.serializer(),
            TakePhotoRequest(
                path = "/some/path"
            )
        )

        val request = factory.createRequest(
            uri = "/api/take_photo",
            method = "POST",
            params = emptyMap(),
            body = jsonBody
        )

        assertTrue(request is TakePhotoRequest)
        assertEquals("/api/take_photo", request.uri)
        assertEquals("/some/path", (request as TakePhotoRequest).path)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testUnknownUri() {
        factory.createRequest(
            uri = "/api/unknown",
            method = "GET",
            params = emptyMap(),
            body = ""
        )
    }
}