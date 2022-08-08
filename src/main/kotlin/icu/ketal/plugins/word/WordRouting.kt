package icu.ketal.plugins.word

import io.ktor.server.application.Application
import io.ktor.server.routing.Routing
import io.ktor.server.routing.routing

interface WordRouting {
    val routing: Routing
}

fun Application.configureWordRouting() {
    routing {
        with(object : WordRouting {
            override val routing = this@routing
        }) {
            getSearchResult()
            getWordDetail()
            getBasicLearningData()
        }
    }
}
