package icu.ketal.plugins.user

import icu.ketal.dao.User
import icu.ketal.data.ServiceError
import io.ktor.server.application.Application
import io.ktor.server.routing.Routing
import io.ktor.server.routing.routing
import org.jetbrains.exposed.sql.transactions.transaction

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

context(UserRouting)
fun check(id: Int, token: String?): ServiceError? {
    val user = transaction {
        User.findById(id)
    }
    if (user == null) {
        return ServiceError.USER_NOTFOUND
    } else if (token == null) {
        return ServiceError.BAD_REQUEST
    } else if (user.token != token) {
        return ServiceError.UNAUTHORIZED
    }
    return null
}
