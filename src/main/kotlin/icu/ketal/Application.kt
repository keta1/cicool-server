package icu.ketal

import icu.ketal.plugins.configureRouting
import icu.ketal.utils.PORT_DEV
import icu.ketal.utils.logger
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    val env = applicationEngineEnvironment {
        log = logger

        connector {
            port = PORT_DEV
        }

        module {
            configureRouting()
        }
    }

    embeddedServer(Netty, env).start(wait = true)
}
