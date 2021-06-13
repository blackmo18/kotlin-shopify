package io.github.blackmo18.shopify.repository

import io.github.blackmo18.shopify.model.payload.incoming.AuthTokenResponse
import io.github.blackmo18.shopify.model.payload.outgoing.AuthTokenRequest
import io.github.blackmo18.shopify.utils.RequestClient
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.core.*

class RequestAuthTokenRepo(
) {
    suspend fun requestToken (
        shopName: String,
        payload: AuthTokenRequest
    ) = RequestClient.init().use {
        it.post<AuthTokenResponse>("https://${shopName}/admin/oauth/access_token") {
            contentType(ContentType.Application.Json)
            body = payload
        }
    }
}