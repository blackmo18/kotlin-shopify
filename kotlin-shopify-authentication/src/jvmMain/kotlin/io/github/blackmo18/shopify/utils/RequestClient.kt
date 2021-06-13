package io.github.blackmo18.shopify.utils

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*

actual object RequestClient {
    actual fun init() : HttpClient {
        return HttpClient(CIO) {
            install(Logging) {
                level = LogLevel.INFO
                logger = Logger.DEFAULT
            }
            install(JsonFeature) {
                serializer = KotlinxSerializer(kotlinx.serialization.json.Json{
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
        }
    }
}