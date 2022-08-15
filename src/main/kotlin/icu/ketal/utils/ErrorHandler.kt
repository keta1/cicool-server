package icu.ketal.utils

import icu.ketal.data.ServiceError
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

suspend inline fun <T> ApplicationCall.catching(block: ApplicationCall.() -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: Throwable) {
        logger.warn(e.stackTraceToString())
        respondError(e.serviceError())
        Result.failure(e)
    }
}

suspend fun ApplicationCall.respondError(error: ServiceError) {
    this.respond(
        HttpStatusCode.fromValue(error.httpStatusCode),
        error
    )
}

fun Throwable.serviceError() = if (this is ServiceError) this else ServiceError(errmsg = message, t = this)
