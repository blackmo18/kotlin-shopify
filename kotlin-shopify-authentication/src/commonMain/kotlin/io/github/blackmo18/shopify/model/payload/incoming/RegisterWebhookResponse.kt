package io.github.blackmo18.shopify.model.payload.incoming

import kotlinx.serialization.Serializable

@Serializable
data class RegisterWebhookResponse (
    val webhook: WebhookDataResponse
)

@Serializable
data class WebhookDataResponse(
    val id: Long,
    val address: String,
    val topic: String,
    val created_at: String,
    val updated_at: String,
    val format: String,
    val fields: List<String?>? = null,
    val metafield_namespaces: List<String?>? = null,
    val api_version: String,
    val private_metafield_namespaces: List<String?>? = null
)