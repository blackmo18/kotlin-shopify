package io.github.blackmo18.shopify.utils

fun String.getParameters(): Map<String, String> {
    val regex = """(?:\?|&|;)([^=]+)=([^&|;]+)""".toRegex()
    return regex.findAll(this)
        .map {
            val split =  it.value
                .substring(1, it.value.length)
                .split("=")
            val body = split.subList(1, split.size).reduceIndexed { index, current, acc ->
                if (index == 0) current
                else "$acc=$current"
            }
            split[0] to body
        }.toMap()
}
