package icu.ketal.plugins.user

import icu.ketal.dao.User
import icu.ketal.utils.logger
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
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
            if (user == null) {
                call.respond(
                    HttpStatusCode.Forbidden,
                    call.respond(
                        UserLoginResponse(
                            errcode = 403,
                            errmsg = "用户不存在"
                        )
                    )
                )
                return@runCatching
            } else if (cookie == null) {
                call.respond(
                    HttpStatusCode.Forbidden,
                    call.respond(
                        UserLoginResponse(
                            errcode = 403,
                            errmsg = "用户未登录"
                        )
                    )
                )
                return@runCatching
            } else if (user.token != cookie) {
                println(user.token)
                println(cookie)
                call.respond(
                    HttpStatusCode.Forbidden,
                    call.respond(
                        UserLoginResponse(
                            errcode = 403,
                            errmsg = "鉴权失败"
                        )
                    )
                )
                return@runCatching
            }
            call.respond(
                UserInfoResponse(
                    errcode = 0,
                    data = UserInfoResponse.UserInfo(user)
                )
            )
        }.onFailure {
            logger.warn(it.stackTraceToString())
            call.respond(
                HttpStatusCode.Forbidden,
                call.respond(
                    UserLoginResponse(
                        errcode = 403,
                        errmsg = it.message
                    )
                )
            )
        }
    }
}

context(UserRouting)
@Serializable
data class UserInfoRequest(
    var id: Int = -1
)

context(UserRouting)
@Serializable
data class UserInfoResponse(
    val errcode: Int,
    val errmsg: String? = null,
    val data: UserInfo? = null,
) {
    @Serializable
    data class UserInfo(
        val nickName: String,
        val avatarPic: String,
        val createTime: Long,
        val lastLogin: Long,
        val bookId: Int
    ) {
        constructor(user: User) : this(
            nickName = user.nickName,
            avatarPic = user.avatarPic,
            createTime = user.createTime,
            lastLogin = user.lastLogin,
            bookId = user.bookId
        )
    }
}
