package io.github.blackmo18.shopify.model.payload.outgoing

import kotlinx.serialization.Serializable

@Serializable
data class AuthTokenRequest (
    val client_id: String,
    val client_secret: String,
    val code: String
)