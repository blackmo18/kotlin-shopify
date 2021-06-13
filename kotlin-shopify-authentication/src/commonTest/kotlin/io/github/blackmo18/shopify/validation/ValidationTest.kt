package io.github.blackmo18.shopify.validation

import io.github.blackmo18.shopify.model.payload.outgoing.AuthTokenRequest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test


class ValidationTest {
    @Test
    fun serializationTest() {
        val data = AuthTokenRequest("ok", "ok", "ok")
        val serialized = Json.encodeToString(data)
    }
}