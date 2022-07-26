package icu.ketal

import icu.ketal.plugins.configureRouting
import icu.ketal.plugins.statistic.configureStatisticRouting
import icu.ketal.plugins.user.configureUserRouting
import icu.ketal.plugins.word.configureWordRouting
import icu.ketal.utils.DBUtils
import icu.ketal.utils.PORT_DEV
import icu.ketal.utils.logger
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

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
            configureWordRouting()
            configureStatisticRouting()
        }
    }

    embeddedServer(Netty, env).start(wait = true)
}
