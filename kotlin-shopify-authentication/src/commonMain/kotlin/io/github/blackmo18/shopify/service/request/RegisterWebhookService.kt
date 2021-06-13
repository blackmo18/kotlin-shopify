package io.github.blackmo18.shopify.service.request

import io.github.blackmo18.shopify.model.context.WebhookInstallationCtx
import io.github.blackmo18.shopify.repository.RegisterWebhookRepo
import io.github.blackmo18.shopify.validation.hooksPayload

open class RegisterWebhookService(private val registerWebhookRepo: RegisterWebhookRepo, private val webhookInstallationCtx: WebhookInstallationCtx) {
    suspend fun requestInstallHook(shopAccessToken: String, shopDomain: String) {
        val payloads = hooksPayload(webhookInstallationCtx)
        registerWebhookRepo.registerWebHooks(payloads, shopAccessToken, shopDomain) { response ->
            println("Webhook installed :: $shopDomain :: ${response.webhook.topic}")
        }
    }
}