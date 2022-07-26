package icu.ketal.plugins

import icu.ketal.dao.User
import icu.ketal.table.UserDb
import icu.ketal.utils.TimeUtil
import icu.ketal.utils.WechatUtils
import icu.ketal.utils.logger
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureUserRouting() {
    routing {
        post("cicool/user/login") {
            kotlin.runCatching {
                val req = call.receive<UserLoginRequest>()
                val session = WechatUtils.code2Session(req.code)
                if (session.errCode != 0) {
                    call.respond(
                        HttpStatusCode.Forbidden,
                        UserLoginResponse(
                            errcode = session.errCode,
                            errmsg = session.errMsg
                        )
                    )
                    return@runCatching
                }
                val user = transaction {
                    val user = User.find { UserDb.openId eq session.openId }.firstOrNull()
                    if (user == null) {
                        User.new {
                            openId = session.openId
                            avatarPic = req.avatarUrl
                            nickName = req.nickName
                            createTime = TimeUtil.now
                            lastLogin = createTime
                        }
                    } else {
                        user.lastLogin = TimeUtil.now
                        user
                    }
                }
                call.respond(
                    UserLoginResponse(
                        errcode = 0,
                        data = UserLoginResponse.UserInfo.fromUser(user)
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
}

@Serializable
data class UserLoginRequest(
    val code: String,
    val nickName: String,
    val avatarUrl: String
)

@Serializable
data class UserLoginResponse(
    val errcode: Int,
    val errmsg: String? = null,
    val data: UserInfo? = null,
) {
    @Serializable
    data class UserInfo(
        val id: Int,
        val avatarPic: String,
        val nickName: String,
        val createTime: Long,
        val lastLogin: Long,
        val bookId: Int
    ) {
        companion object {
            fun fromUser(user: User): UserInfo {
                return UserInfo(
                    id = user.id.value,
                    avatarPic = user.avatarPic,
                    nickName = user.nickName,
                    createTime = user.createTime,
                    lastLogin = user.lastLogin,
                    bookId = user.bookId
                )
            }
        }
    }
}