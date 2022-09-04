package icu.ketal.plugins.user

import icu.ketal.dao.User
import icu.ketal.data.ServiceError
import io.ktor.server.application.Application
import io.ktor.server.request.ApplicationRequest
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
            changeUserInfo()
            uploadAvatar()
            changeWordBook()
            changeUserSetting()
        }
    }
}

fun check(id: Int?, request: ApplicationRequest) {
    val token = request.cookies["TOKEN"]
    if (id == null || token == null) {
        throw ServiceError.BAD_REQUEST
    }
    val user = transaction {
        User.findById(id)
    }
    if (user == null) {
        throw ServiceError.USER_NOT_FOUND
    } else if (user.token != token) {
        throw ServiceError.UNAUTHORIZED
    }
}
