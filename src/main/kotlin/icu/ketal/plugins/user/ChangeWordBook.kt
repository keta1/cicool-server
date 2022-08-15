package icu.ketal.plugins.user

import icu.ketal.dao.User
import icu.ketal.data.ServiceError
import icu.ketal.utils.catching
import icu.ketal.utils.respondError
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction

context(UserRouting)
fun changeWordBook() {
    routing.post("cicool/user/changeWordBook") {
        call.catching {
            val (userId, bookId) = receive<ChangeWordBookReq>()
            check(userId, request)
            transaction {
                User.findById(userId)!!.bookId = bookId
            }
            respondError(ServiceError.OK)
        }
    }
}

@Serializable
data class ChangeWordBookReq(var userId: Int, var bookId: Int)
