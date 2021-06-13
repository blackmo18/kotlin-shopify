package io.github.blackmo18.shopify.functions

import io.github.blackmo18.shopify.model.webhook.RegisterHook
import io.github.blackmo18.shopify.model.webhook.Webhook

fun hook (topic: String, host: String, callback: String? = null): RegisterHook {
    val hook = Webhook(topic, "$host/api/webhook/$topic")
    return RegisterHook(hook)
}