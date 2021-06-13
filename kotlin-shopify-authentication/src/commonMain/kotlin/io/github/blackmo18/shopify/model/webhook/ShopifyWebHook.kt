package io.github.blackmo18.shopify.model.webhook


data class ShopifyWebHook(
    val webhook: Webhook,
    val handler: HandlerTemplate<*>
)