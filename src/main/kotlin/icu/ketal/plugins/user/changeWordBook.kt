package icu.ketal.plugins.user

import icu.ketal.dao.User
import icu.ketal.data.ServiceError
import icu.ketal.utils.logger
import icu.ketal.utils.respondError
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction

context(UserRouting)
fun changeWordBook() {
    routing.post("cicool/user/changeWordBook") {
        kotlin.runCatching {
            val (userId, bookId) = call.receive<ChangeWordBookReq>()
            val cookie = call.request.cookies["TOKEN"]
            check(userId, cookie)?.let {
                call.respondError(it)
                return@runCatching
            }
            transaction {
                User.findById(userId)!!.bookId = bookId
            }
            call.respondError(ServiceError.OK)
        }.onFailure {
            logger.warn(it.stackTraceToString())
            call.respondError(ServiceError.INTERNAL_SERVER_ERROR)
        }
    }
}

@Serializable
data class ChangeWordBookReq(
    var userId: Int,
    var bookId: Int
)
