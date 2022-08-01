package icu.ketal

import icu.ketal.plugins.configureRouting
import icu.ketal.plugins.user.configureUserRouting
import icu.ketal.utils.DBUtils
import icu.ketal.utils.PORT_DEV
import icu.ketal.utils.logger
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    DBUtils.prepare()

    val env = applicationEngineEnvironment {
        log = logger

        connector {
            port = PORT_DEV
        }

        module {
            configureRouting()
            configureUserRouting()
        }
    }

    embeddedServer(Netty, env).start(wait = true)
}
