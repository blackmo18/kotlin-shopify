package io.github.blackmo18.shopify.model

import io.github.blackmo18.shopify.model.context.ShopifyAccessCtx
import io.github.blackmo18.shopify.model.context.WebhookInstallationCtx

data class WebhookInstallationAccess(
    override val shopifyAccessCtx: ShopifyAccessCtx
): WebhookInstallationCtx {
    override var topics: List<String> = listOf()
}