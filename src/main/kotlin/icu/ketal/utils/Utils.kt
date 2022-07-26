package icu.ketal.utils

import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger by lazy {
    LoggerFactory.getLogger("cicool-server")
}

suspend inline fun ApplicationCall.receiveBytesAndDecode(): String = receive<ByteArray>().toString()
