package icu.ketal.plugins.statistic

import io.ktor.server.application.Application
import io.ktor.server.routing.Routing
import io.ktor.server.routing.routing

interface StatisticRouting {
    val routing: Routing
}

fun Application.configureStatisticRouting() {
    routing {
        with(object : StatisticRouting {
            override val routing = this@routing
        }) {
            getWBLearnData()
            getAllWBData()
            getSingleWBData()
            getAllLearnData()
            getTodayLearnData()
            getDailySum()
            getNoteBookWord()
            getBkLearnedWord()
            getBkMasteredWord()
            getBkUnlearnedWord()
            getBkWord()
            getLearnedWord()
            getMasteredWord()
            getReviewWord()
            getTodayLearnWord()
            getTodayReviewWord()
        }
    }
}
