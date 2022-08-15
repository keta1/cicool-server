package icu.ketal.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger by lazy {
    LoggerFactory.getLogger("cicool-server")
}

fun genSalt(length: Int): String = buildString {
    repeat(length) {
        append((('a'..'z') + ('A'..'Z') + ('0'..'9')).random())
    }
}

fun isTranslation(keyword: String): Boolean = keyword.contains(Regex("[\u4e00-\u9fa5]"))
