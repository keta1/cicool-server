package icu.ketal.plugins.user

import icu.ketal.dao.User
import icu.ketal.data.ServiceError
import icu.ketal.utils.logger
import icu.ketal.utils.respondError
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction

context(UserRouting)
fun getUserInfo() {
    routing.post("cicool/user/getUserInfo") {
        kotlin.runCatching {
            val req = call.receive<UserInfoRequest>()
            val user = transaction {
                User.findById(req.id)
            }
            val cookie = call.request.cookies["TOKEN"]
            check(req.id, cookie)?.let {
                call.respondError(it)
                return@runCatching
            }
            call.respond(
                UserInfoResponse(
                    errcode = 0,
                    data = UserInfoResponse.UserInfo(user!!)
                )
            )
        }.onFailure {
            logger.warn(it.stackTraceToString())
            call.respondError(ServiceError.INTERNAL_SERVER_ERROR)
        }
    }
}

@Serializable
data class UserInfoRequest(
    val id: Int = -1
)

@Serializable
data class UserInfoResponse(
    val errcode: Int,
    val errmsg: String? = null,
    val data: UserInfo? = null,
) {
    @Serializable
    data class UserInfo(
        val nickName: String,
        val createTime: Long,
        val lastLogin: Long,
        val bookId: Int
    ) {
        constructor(user: User) : this(
            nickName = user.nickName,
            createTime = user.createTime,
            lastLogin = user.lastLogin,
            bookId = user.bookId
        )
    }
}
