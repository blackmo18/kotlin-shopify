package io.github.blackmo18.shopify.service.request

import io.github.blackmo18.shopify.model.payload.incoming.AuthTokenResponse
import io.github.blackmo18.shopify.model.payload.outgoing.AuthTokenRequest
import io.github.blackmo18.shopify.repository.RequestAuthTokenRepo

class RequestAuthTokenService (
    private val repo: RequestAuthTokenRepo
) {
    suspend fun requestToken(shopName: String, apiKey: String, apiSecretKey: String, code: String): AuthTokenResponse {
        val payload = AuthTokenRequest( client_id = apiKey, client_secret = apiSecretKey, code = code)
        return repo.requestToken(shopName, payload)
    }
}