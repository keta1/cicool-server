package icu.ketal.utils

import icu.ketal.data.ServiceError
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
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

fun isTranslation(keyword: String): Boolean {
    return keyword.contains(Regex("[\u4e00-\u9fa5]"))
}

suspend fun ApplicationCall.respondError(error: ServiceError) {
    this.respond(
        HttpStatusCode.fromValue(error.httpStatusCode),
        error
    )
}
