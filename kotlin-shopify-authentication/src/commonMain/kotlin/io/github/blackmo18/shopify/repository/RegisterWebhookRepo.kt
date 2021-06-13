package io.github.blackmo18.shopify.repository

import io.github.blackmo18.shopify.model.payload.incoming.RegisterWebhookResponse
import io.github.blackmo18.shopify.model.webhook.RegisterHook
import io.github.blackmo18.shopify.model.webhook.Webhook
import io.github.blackmo18.shopify.utils.RequestClient
import io.ktor.client.request.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect

class RegisterWebhookRepo {
    suspend fun registerWebHooks(hooks: List<RegisterHook>, accessToken: String,
                                 shopDomain: String, callback: (response: RegisterWebhookResponse)-> Unit) {
        val client = RequestClient.init()
        hooks.asFlow().collect {  hook ->
            val response: RegisterWebhookResponse = client.post("https://${shopDomain}/admin/api/2021-01/webhooks.json") {
                headers {
                    append("Content-Type", "application/json")
                    append("X-Shopify-Access-Token", accessToken)
                }
                body = hook
            }
            callback(response)
        }
        client.close()
    }

    suspend fun registerWebHook(hook: Webhook, accessToken: String, shop: String ) = RequestClient.init().use {
        it.post<RegisterWebhookResponse> {
            headers {
                append("X-Shopify-Access-Token", accessToken)
            }
            body = hook
        }
    }
}
