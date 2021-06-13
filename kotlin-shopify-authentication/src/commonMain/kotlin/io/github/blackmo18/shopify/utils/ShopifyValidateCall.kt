package io.github.blackmo18.shopify.utils

import kotlin.random.Random

object ShopifyInstallService {
    fun validateShopAndDomain(uri: String, mappedParams: Map<String, String>): Boolean {
        val shopName = mappedParams["shop"] ?: error("Shopify Shop Error")
//        val validShopName = shopName.validateShopName()
//        val validHostName = uri.validateHost()
//        return validShopName && validHostName
        return shopName.validateShopName()
    }
}

object ShopifyNonce {
    private val nonceMapped = mutableMapOf<String, Long>()

    fun acquireNonce(shopName: String): Long {
        val nonce = Random(1_000_000_000).nextLong()
        nonceMapped[shopName] = nonce
        return nonce
    }

    fun validateNonce(shopName: String, nonce: Long): Boolean {
        return (nonceMapped[shopName] == nonce).also {
            nonceMapped.remove(shopName)
        }
    }
}

private fun String.validateHost(): Boolean {
    val regex = """\A(https|http)\:\/\/[a-zA-Z0-9][a-zA-Z0-9\-]*\.myshopify\.com\/""".toRegex()
    return this.matches(regex)
}

private fun String.validateShopName(): Boolean {
    val regex = """\A[a-zA-Z0-9][a-zA-Z0-9\-]*\.myshopify\.com\z""".toRegex()
    return this.matches(regex)
}
