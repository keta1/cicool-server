package icu.ketal.plugins.user

import icu.ketal.dao.User
import icu.ketal.table.UserDb
import icu.ketal.utils.TimeUtil
import icu.ketal.utils.WechatUtils
import icu.ketal.utils.genSalt
import icu.ketal.utils.logger
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction

context(UserRouting)
fun login() {
    routing.post("cicool/user/login") {
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
                        sessionKey = session.sessionKey
                        salt = genSalt(16)
                        createTime = TimeUtil.now
                        lastLogin = createTime
                    }
                } else {
                    user.lastLogin = TimeUtil.now
                    user.sessionKey = session.sessionKey
                    user
                }
            }
            call.respond(
                UserLoginResponse(
                    errcode = 0,
                    data = UserLoginResponse.UserInfo(user)
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
class UserLoginRequest(
    var code: String = ""
)

context(UserRouting)
@Serializable
data class UserLoginResponse(
    val errcode: Int,
    val errmsg: String? = null,
    val data: UserInfo? = null,
) {
    @Serializable
    data class UserInfo(
        val id: Int,
        val token: String,
        val nickName: String,
        val avatarPic: String,
        val createTime: Long,
        val lastLogin: Long,
        val bookId: Int
    ) {
        constructor(user: User) : this(
            id = user.id.value,
            token = user.token,
            nickName = user.nickName,
            avatarPic = user.avatarPic,
            createTime = user.createTime,
            lastLogin = user.lastLogin,
            bookId = user.bookId
        )
    }
}
