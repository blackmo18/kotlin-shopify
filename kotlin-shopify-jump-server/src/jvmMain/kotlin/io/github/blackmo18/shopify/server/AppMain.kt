package io.github.blackmo18.shopify.server

import io.github.blackmo18.shopify.model.enums.ResponseStatus
import io.github.blackmo18.shopify.utils.ShopifyAuthUtils
import io.github.blackmo18.shopify.validation.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.netty.*
import kotlinx.coroutines.runBlocking
import org.slf4j.event.Level

class AppMain

val SHOPIFY_CONTEXT =  authenticationSetup {
    apiKey = "your_app_key"
    apiSecret = "your_app_api_secret_key"
    host = "your_server_host_url"
    scopes = listOf(
        "read_products",
        "write_products",
        "read_customers",
        "read_orders",
        "write_orders",
        "read_fulfillments",
        "read_checkouts",
        "read_locations",
        "read_draft_orders",
        "read_shopify_payments_disputes",
        "read_script_tags",
        "write_script_tags"
    )
    accessType = "offline"
}

val WEBHOOK_SETUP = webhookInstallationSetup(SHOPIFY_CONTEXT) {
    topics = listOf("carts/create")
}

fun main(args: Array<String>): Unit = EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module() = runBlocking {
    install(ContentNegotiation)
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    routing {

        get("/") {
            SHOPIFY_CONTEXT.authenticateInstall(call.request.uri) { isValid, redirect ->
                when {
                    isValid -> call.respondRedirect(redirect!!)
                    else -> call.response.status(HttpStatusCode.Unauthorized)
                }
            }
        }

        get("/auth/callback") {
            val response = SHOPIFY_CONTEXT.onInstallRedirect(call.request.uri) {
                isAuthenticated, redirect ->
                when {
                    isAuthenticated -> call.respondRedirect(redirect!!)
                    else -> call.response.status(HttpStatusCode.Unauthorized)
                }
            }
            when (response.code) {
                ResponseStatus.SUCCESS -> {
                    response.data?.run { WEBHOOK_SETUP.registerHooks(access_token, shop) }
                }
                else -> {
                    TODO("nothing")
                }
            }
        }

        post("/api/webhook/carts/create") {
            val hmac  = call.request.headers["x-shopify-hmac-sha256"]
            val payload = call.receiveText()
            val validated = ShopifyAuthUtils.validateWebhookCall(payload, SHOPIFY_CONTEXT.apiSecret, hmac!!)
            when {
                validated -> {
                    call.respond("ok")
                    println("validated carts/create api call")
                }
                else -> {
                    call.response.status(HttpStatusCode.Unauthorized)
                    println("invalidated carts/create api call")
                }
            }
        }
    }

}