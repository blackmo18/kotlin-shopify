package io.github.blackmo18.shopify.utils

object AuthenticationUtils {
    fun String.md5(): String {
        TODO()
    }
    fun String.sha256(): String {
        TODO()
    }
    fun String.hmac256(): String {
        TODO()
    }
}

expect object HmacVerification {
    fun String.hmacSha256(secretKey: String): String
    fun String.hmacSh256Base64(secretKey: String): String
    fun hashString(input: String, algorithm: String): String
}




