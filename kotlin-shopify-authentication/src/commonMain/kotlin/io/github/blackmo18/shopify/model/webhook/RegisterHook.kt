package io.github.blackmo18.shopify.model.webhook
import kotlinx.serialization.Serializable

@Serializable
data class RegisterHook(
    val webhook: Webhook
)

@Serializable
data class Webhook(val topic: String, val address: String, val format: String = "json")