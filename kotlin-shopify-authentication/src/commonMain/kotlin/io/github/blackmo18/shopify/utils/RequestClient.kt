package io.github.blackmo18.shopify.utils

import io.ktor.client.HttpClient

expect object RequestClient {
    actual fun init(): HttpClient
}