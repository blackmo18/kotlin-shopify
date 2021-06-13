package io.github.blackmo18.shopify.utils

import io.github.blackmo18.shopify.utils.HmacVerification.hmacSha256
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class JsUtilsTest: StringSpec({
    "hmac verification" {
        val crypto = require("crypto")
        val str = "Hello, world"
        val key = "key123"

        val expected = crypto.createHmac("sha256", key).update(str).digest("hex") as String
        val actual = str.hmacSha256(key)
        expected shouldBe actual
    }
})