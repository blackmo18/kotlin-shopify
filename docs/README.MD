<p>
  <img alt="Kotlin" src="https://img.shields.io/badge/kotlin-1.5.10-teal.svg?logo=kotlin" />
  <a href="/>https://repo1.maven.org/maven2/io/github/blackmo18/kotlin-grass-jvm/0.7.1/">
    <img alt="Latest: 0.1.0" src="https://img.shields.io/badge/Latest-0.1.0-blue.svg" target="_blank" />
  </a>
  <a href="https://github.com/blackmo18/kotlin-shopify/actions/workflows/gradle.yml/badge.svg">
    <img src="https://github.com/blackmo18/kotlin-shopify/actions/workflows/gradle.yml/badge.svg" alt="CodeFactor" />
  </a>
  <a href="https://github.com/blackmo18/kotlin-grass/blob/master/LICENSE">
    <img alt="License: Apache License 2.0" src="https://img.shields.io/badge/License-Apache License 2.0-orange.svg" target="_blank" />
  </a>
<a href="https://www.codefactor.io/repository/github/blackmo18/kotlin-shopify"><img src="https://www.codefactor.io/repository/github/blackmo18/kotlin-shopify/badge" alt="CodeFactor" /></a>
</p>

#Kotlin-Shopify Authentication
> Simple Straightforward Authentication Library for Shopify Application Setup in Kotlin


##Features
1. Application Authentication
2. Webhook Integration
3. Webhook Incoming request validation
4. Planned multiplatform

## Usage
#### Gradle DSL:
```kotlin
implementation("io.github.blackmo18:kotlin-shopify-authentication-jvm:0.1.0")
```

#### Maven:
```xml
<dependency>
    <groupId>io.github.blackmo18</groupId>
    <artifactId>kotlin-shopify-authentication-jvm</artifactId>
    <version>0.1.0</version>
</dependency>
```

### 1. Declare Your Shopify Authentication Context
```kotlin

val SHOPIFY_CONTEXT =  authenticationSetup {
    apiKey = "your_app_key"
    apiSecret = "your_app_api_secret_key"
    host = "your_server_host_url"
    scopes = listOf( // declare your access scopes here
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
    accessType="offline" // declare you access whether online or offline
}
```

### 2. Declare Your Webhook Context
```kotlin

val WEBHOOK_SETUP = webhookInstallationSetup(SHOPIFY_CONTEXT) {
    topics = listOf(
        "carts/create",
        "carts/update"
        ...
    )
}
```
### 3. Set up your Authentication Server and Callback endpoints

a.) `authenticateInstall(callingURL: String)`

validates incoming request from shopify and exposes a callback function that
   determines whether the request is valid.
* `isValid` flag wether the call is valid
* `redirect` should be returned if the call is valid
  
b.) `onInstallRedirect(callingURL: String)`

validates incoming request from shopify on app install, 
   determines whether the request is valid.
* `isValid`
* the `redirect` should be returned if the call is valid or override redirect url
  
c.) `registerHooks(shopAccessToken: String, shopDomain: String)`

register all webhook topics declared in Webhook Setup

### 4. Create Webhook Endpoint

a.) note that when a webhook is created, it will register the endpoint to the following format to shopify
`{your url HOST}/api/webhook/{webhook_topic}` so you must CAREFULLY match your endpoint to the latter format

b.) `ShopifyAuthUtils.validateWebhookCall(payload: String, apiSecret: String, hmac: String)`

validates whether the incoming webhook call actually came from shopify

   
### For example we are using ktor as server
```kotlin
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
```