package io.github.blackmo18.shopify.validation

import io.github.blackmo18.shopify.constatns.VerificationMessages
import io.github.blackmo18.shopify.service.cahcing.SimpleCachingService
import io.github.blackmo18.shopify.model.Response
import io.github.blackmo18.shopify.model.ShopifyAccess
import io.github.blackmo18.shopify.model.context.ShopifyAccessCtx
import io.github.blackmo18.shopify.model.enums.ResponseStatus
import io.github.blackmo18.shopify.model.payload.incoming.AuthTokenResponse
import io.github.blackmo18.shopify.repository.RequestAuthTokenRepo
import io.github.blackmo18.shopify.service.request.RequestAuthTokenService
import io.github.blackmo18.shopify.utils.ShopifyAuthUtils
import io.github.blackmo18.shopify.utils.ShopifyInstallService
import io.github.blackmo18.shopify.utils.ShopifyNonce
import io.github.blackmo18.shopify.utils.getParameters
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

private const val STATE = "state"
private const val CODE = "code"
private const val SHOP = "shop"

/**
 * validates incoming Shopify request on app installation
 * @param url shopify store calling url
 * @param block callback function
 *
 *  * isValid: boolean - determines whether the request is valid
 *  * redirect: string - shopify redirect url
 * @author blackmo18
 */
suspend fun ShopifyAccessCtx.onInstallRedirect(requestingUrl: String, block: (suspend (isAuthenticated: Boolean, redirect: String?) -> Unit)? = null) = coroutineScope  {
    // get request parameters

    val mappedParams = requestingUrl.getParameters()

    val state = mappedParams[STATE] ?: error(VerificationMessages.ERROR_SHOPIFY_STATE)
    val code = mappedParams[CODE] ?: error(VerificationMessages.ERROR_SHOPIFY_CODE)
    val shopDomain = mappedParams[SHOP] ?: error(VerificationMessages.ERROR_SHOPIFY_SHOP)

    val defaultShopRedirectUrl = defaultRedirect(shopDomain)

    var validSha: Boolean? = null
    var validCall: Boolean? = null
    var validNonce: Boolean? = null

    //validation checks
    val jobSha = async { ShopifyAuthUtils.verifyInstallHmac(mappedParams, apiSecret).also { validSha = it } }
    val jobCall = async { ShopifyInstallService.validateShopAndDomain(requestingUrl, mappedParams).also { validCall = it } }
    val jobNonce = async { ShopifyNonce.validateNonce(shopDomain, state.toLong()).also { validNonce = it } }

    when {
        jobNonce.await()  && jobSha.await() && jobCall.await() -> {
            val authResponse = getShopToken(shopDomain, apiKey, apiSecret, code)
                .apply { shop = shopDomain }
            SimpleCachingService.mapTokenKeys[shopDomain] = authResponse.access_token
            block?.invoke(true, defaultShopRedirectUrl)
            Response(ResponseStatus.SUCCESS, VerificationMessages.SHOP_VERIFIED, authResponse)
        }
        validCall != null && validCall == false -> {
            block?.invoke(false, defaultShopRedirectUrl)
            Response(ResponseStatus.ERROR, VerificationMessages.INVALID_SHOP_DOMAIN + " :: $requestingUrl")
        }
        validSha != null && validSha == false -> {
            block?.invoke(false, defaultShopRedirectUrl)
            Response(ResponseStatus.ERROR, VerificationMessages.INVALID_SHA + ": $requestingUrl")
        }
        validNonce != null && validNonce == false -> {
            block?.invoke(false, defaultShopRedirectUrl)
            Response(ResponseStatus.ERROR, VerificationMessages.INVALID_NONCE + ": $requestingUrl")
        }
        else -> {
            block?.invoke(false, defaultShopRedirectUrl)
            Response(ResponseStatus.ERROR, VerificationMessages.INVALIDATION_ERROR)
        }
    }
}

private suspend fun getShopToken(shopName: String, apiKey: String, apiSecret: String, code: String): AuthTokenResponse =
    RequestAuthTokenService(RequestAuthTokenRepo())
        .requestToken(shopName, apiKey, apiSecret, code)


/**
 * validates incoming Shopify request on app request token
 * @param url shopify store calling url
 * @param block callback function
 *
 *  * isValid: boolean - determines whether the request is valid
 *  * redirect: string - shopify redirect url
 * @author blackmo18
 */
suspend fun ShopifyAccessCtx.authenticateInstall(url: String, block: suspend (isValid: Boolean, redirect: String? ) -> Unit) {
    val params = url.getParameters()
    val validSha = ShopifyAuthUtils.verifyInstallHmac(params, apiSecret)

    if (validSha) {
        val nonce = ShopifyNonce.acquireNonce(params["shop"] ?: error(VerificationMessages.INVALID_SHOP_NAME))
        val redirectValue = "https://${params["shop"]}/admin/oauth/authorize?" +
                "client_id=${apiKey}&scope=${scopesTonInlineString(scopes)}&" +
                "state=$nonce&redirect_uri=${host}/auth/callback&" +
                "grant_options[]=online${access(accessType)}"
        block(validSha, redirectValue)
    } else {
       block(validSha, null)
    }
}

private fun access(value: String): String {
    return if (value == "offline") return "&grant_options[]=offline"
    else ""
}

private fun defaultRedirect(shopDomain: String) = "https://$shopDomain/admin/apps"

fun scopesTonInlineString(scopes: List<String>): String {
    return  scopes.reduce { acc, s ->"$acc,$s" }
}

/**
 * @return creates shopify [ShopifyAccessCtx] authentication access context
 * @author blackmo18
 */
inline fun authenticationSetup(block: ShopifyAccess.() -> Unit): ShopifyAccessCtx {
    return ShopifyAccess().apply(block)
}