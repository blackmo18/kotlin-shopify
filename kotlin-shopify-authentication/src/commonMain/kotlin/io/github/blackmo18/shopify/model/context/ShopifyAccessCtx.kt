package io.github.blackmo18.shopify.model.context

/**
 * Shopify Access Context
 *  * apiKey - application api key
 *  * apiSecret - application api secret key token
 *  * host - application host url
 *  * scopes - application scopes
 *  * accessType - offline | online
 *  @property apiKey - application api key
 *  @property apiSecret - application api secret key token
 *  @property host - application host url
 *  @property scopes - application scopes
 *  @property accessType - offline | online
 *  @author blackmo18
 */
interface ShopifyAccessCtx {
    val apiKey: String
    val apiSecret: String
    val host: String
    val scopes: List<String>
    val accessType: String
}

/**
 * Shopify Webhook Access Context
 *  * topics - shopify webhooks topics to register
 *  * shopifyAccessCtx - shopify application access context [ShopifyAccessCtx]
 *  @property topics - shopify webhooks topics to register
 *  @property shopifyAccessCtx - shopify application access context [ShopifyAccessCtx]
 *  @author blackmo18
 */
interface WebhookInstallationCtx {
    val topics: List<String>
    val shopifyAccessCtx: ShopifyAccessCtx
}
