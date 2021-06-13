package io.github.blackmo18.shopify.utils

import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.binary.Hex
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

actual object HmacVerification{
    actual fun String.hmacSha256(secretKey: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        val secret = SecretKeySpec(secretKey.toByteArray(), "HmacSHA256" )
        mac.init(secret)
        return Hex.encodeHexString(mac.doFinal(this.toByteArray()))
    }

    actual fun String.hmacSh256Base64(secretKey: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        val secret = SecretKeySpec(secretKey.toByteArray(), "HmacSHA256" )
        mac.init(secret)
        return Base64.encodeBase64String(mac.doFinal(this.encodeToByteArray()))
    }

    actual fun hashString(input: String, algorithm: String): String {
        return MessageDigest
            .getInstance(algorithm)
            .digest(input.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }
    }
}
