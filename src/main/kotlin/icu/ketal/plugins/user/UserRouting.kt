package icu.ketal.plugins.user

import io.ktor.server.application.*
import io.ktor.server.routing.*

interface UserRouting {
    val routing: Routing
}

fun Application.configureUserRouting() {
    routing {
        with(object : UserRouting {
            override val routing = this@routing
        }) {
            login()
            getUserInfo()
        }
    }
}
