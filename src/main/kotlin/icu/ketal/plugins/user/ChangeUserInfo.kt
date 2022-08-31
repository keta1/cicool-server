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
fun changeUserInfo() {
    routing.post("cicool/user/changeUserInfo") {
        call.catching {
            val (id, nickName, avatarPic) = receive<ChangeUserInfoRequest>()
            check(id, request)
            transaction {
                val user = User.findById(id)!!
                nickName?.let { user.nickName = it }
                avatarPic?.let { user.avatarPic = it }
            }
            respondError(ServiceError.OK)
        }
    }
}

@Serializable
data class ChangeUserInfoRequest(
    var userId: Int,
    var nickName: String? = null,
    var avatarPic: String? = null
)
