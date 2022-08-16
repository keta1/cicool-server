package icu.ketal.plugins.user

import icu.ketal.dao.User
import icu.ketal.utils.catching
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction

context(UserRouting)
fun getUserInfo() {
    routing.post("cicool/user/getUserInfo") {
        call.catching {
            val (id) = receive<UserInfoRequest>()
            check(id, request)
            val rsq = transaction {
                val user = User.findById(id)!!
                UserInfoResponse(data = UserInfoResponse.UserInfo(user))
            }
            respond(rsq)
        }
    }
}

@Serializable
data class UserInfoRequest(val userId: Int)

@Serializable
data class UserInfoResponse(
    val errcode: Int = 0,
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
