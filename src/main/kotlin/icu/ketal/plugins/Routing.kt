package icu.ketal.plugins

import icu.ketal.data.Version
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/cicool") {
            call.respond(Version())
        }
    }
}
