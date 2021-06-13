package io.github.blackmo18.shopify.utils

import io.github.blackmo18.shopify.utils.HmacVerification.hmacSha256
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.WordSpec

class JvmUtilTest: WordSpec() {
    init {
        "Test Hmac verification" should {
            "validate String hmac 256" {
                val result = "sample".hmacSha256("123")
                result shouldNotBe null
            }
        }
    }
}