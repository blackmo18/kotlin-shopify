package io.github.blackmo18.shopify.utils

import io.github.blackmo18.shopify.utils.HmacVerification.hmacSh256Base64
import io.github.blackmo18.shopify.utils.HmacVerification.hmacSha256

private const val HMAC = "hmac"

/**
 * Shopify Authentication Util functions
 * @author blackmo18
 */
object ShopifyAuthUtils {
    /**
     * verify url params agains hmac given by shopify
     * @param mappedParams Shopify mapped params
     * @param secretKey application secret key
     */
    fun verifyInstallHmac(mappedParams: Map<String, String>, secretKey: String): Boolean {
        val hmacValue = mappedParams[HMAC]
        val mutatedMap = mappedParams.toMutableMap().also { it.remove(HMAC) }
        val message =  mutatedMap
            .map { "${it.key}=${it.value}" }
            .reduce { acc, s -> "$acc&$s"  }
        return hmacValue == message.hmacSha256(secretKey)
    }

    fun verifyInstallHmac(url: String, secretKey: String): Boolean {
        val mappedParams = url.getParameters()
        val hmacValue = mappedParams[HMAC]
        val mutatedMap = mappedParams.toMutableMap().also { it.remove(HMAC) }
        val message =  mutatedMap
            .map { "${it.key}=${it.value}" }
            .reduce { acc, s -> "$acc&$s"  }
        return hmacValue == message.hmacSha256(secretKey)
    }

    /**
     * Verify whether incoming call actually came from shopify
     * @param payload payload in string
     * @param secretKey application secret key
     * @param hmac HMAC from shopify
     */
    fun validateWebhookCall (payload: String, secretKey: String, hmac: String): Boolean {
        val calculatedHmac = payload.hmacSh256Base64(secretKey)
        return hmac ==  calculatedHmac
    }
}

