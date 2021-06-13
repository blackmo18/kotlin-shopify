package io.github.blackmo18.shopify.utils

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class UtilsTest: StringSpec({
    "test url parameter parsed into map" {
        val expected = mapOf(
            "hello" to "hi",
            "value1" to "expect1",
            "value2" to "expect2"
        )
        val url = "www.something.com/?hello=hi&value1=expect1&value2=expect2"
        val actual = url.getParameters()
        expected shouldBe actual
    }
    "test url parameter parsing into map without slash" {
        val expected = mapOf(
            "hello" to "hi",
            "value1" to "expect1",
            "value2" to "expect2"
        )
        val url = "www.something.com?hello=hi&value1=expect1&value2=expect2"
        val actual = url.getParameters()
        expected shouldBe actual
    }
    "test url parameter using semicolon delimiter" {
        val expected = mapOf(
            "hello" to "hi",
            "value1" to "expect1",
            "value2" to "expect2"
        )
        val url = "www.something.com?hello=hi;value1=expect1;value2=expect2"
        val actual = url.getParameters()
        expected shouldBe actual
    }
})