package io.github.blackmo18.shopify.model

import io.github.blackmo18.shopify.model.enums.ResponseStatus


data class Response<T>(
    val code: ResponseStatus,
    val message: String,
    val data: T? = null,
    val status: Int? = null,
)
