package io.github.blackmo18.shopify.utils

import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*

actual object RequestClient {
    actual fun init() : HttpClient {
        return HttpClient(Js) {
            install(Logging) {
                level = LogLevel.INFO
                logger = Logger.DEFAULT
            }
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }
    }
}
