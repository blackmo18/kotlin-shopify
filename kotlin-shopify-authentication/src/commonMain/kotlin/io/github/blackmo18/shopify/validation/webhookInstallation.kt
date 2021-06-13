package io.github.blackmo18.shopify.validation

import io.github.blackmo18.shopify.functions.hook
import io.github.blackmo18.shopify.model.WebhookInstallationAccess
import io.github.blackmo18.shopify.model.context.ShopifyAccessCtx
import io.github.blackmo18.shopify.model.context.WebhookInstallationCtx
import io.github.blackmo18.shopify.model.webhook.RegisterHook
import io.github.blackmo18.shopify.repository.RegisterWebhookRepo
import io.github.blackmo18.shopify.service.request.RegisterWebhookService


/**
 * @return creates and initializes Shopify Webhook Access Context [WebhookInstallationCtx]
 */
inline fun webhookInstallationSetup (shopifyAccessCtx: ShopifyAccessCtx, block: WebhookInstallationAccess.() -> Unit): WebhookInstallationCtx {
    return WebhookInstallationAccess(shopifyAccessCtx).apply(block)
}

/**
 * @param context Shopify Webhook Access Context [WebhookInstallationCtx]
 * @return webhook register payload
 * @author blackmo18
 */
fun  hooksPayload(context: WebhookInstallationCtx): List<RegisterHook> {
    return context.topics.map { topic ->
        hook(topic, context.shopifyAccessCtx.host)
    }
}

/**
 * registers all webhooks declared in Shopify Webhook Access Context [WebhookInstallationCtx]
 * @param shopAccessToken stores access token
 * @param shopDomain store shop domain
 * @author blackmo18
 */
suspend fun WebhookInstallationCtx.registerHooks(shopAccessToken: String, shopDomain: String) {
    val service = RegisterWebhookService(RegisterWebhookRepo(), this)
    service.requestInstallHook(shopAccessToken, shopDomain)
}