package io.github.blackmo18.shopify.model.payload.incoming

interface WebhookPayloadTemplate<T> {
    val topic: String
    val domain: String
    val payload: T
}