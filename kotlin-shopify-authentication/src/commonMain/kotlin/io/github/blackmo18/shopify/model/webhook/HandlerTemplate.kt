package io.github.blackmo18.shopify.model.webhook

import io.github.blackmo18.shopify.model.payload.incoming.WebhookPayloadTemplate

abstract class HandlerTemplate<T> {
    abstract  fun handler()
}