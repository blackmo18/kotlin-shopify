package io.github.blackmo18.shopify.utils

actual object HmacVerification{
    private val crypto = require("crypto")
    actual fun String.hmacSha256(secretKey: String): String {
        return crypto.createHmac("sha256", secretKey).update(this).digest("hex") as String
    }
    actual fun String.hmacSh256Base64(secretKey: String): String {
        return  crypto.createHmac("sha256", secretKey)
            .update(this)
            .digest("base64") as String
    }
    actual fun hashString(input: String, algorithm: String): String {
        TODO()
    }
}

