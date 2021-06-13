package io.github.blackmo18.shopify.model

import io.github.blackmo18.shopify.model.context.ShopifyAccessCtx

class ShopifyAccess: ShopifyAccessCtx {
    override var apiKey = ""
    override var apiSecret =""
    override var host = ""
    override var scopes = listOf<String>()
    override var accessType = "onlline"
}
