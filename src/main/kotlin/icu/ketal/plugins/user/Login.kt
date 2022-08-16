package icu.ketal.plugins.user

import icu.ketal.dao.User
import icu.ketal.data.ServiceError
import icu.ketal.table.UserDb
import icu.ketal.utils.WechatUtils
import icu.ketal.utils.catching
import icu.ketal.utils.genSalt
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction

context(UserRouting)
fun login() {
    routing.post("cicool/user/login") {
        call.catching {
            val (code) = receive<UserLoginRequest>()
            val session = WechatUtils.code2Session(code)
            if (session.errCode != 0) {
                throw ServiceError(HttpStatusCode.BadRequest.value, session.errCode, session.errMsg)
            }
            val rsq = transaction {
                val user = User.find { UserDb.openId eq session.openId }.firstOrNull()
                val uu = if (user == null) {
                    User.new {
                        openId = session.openId
                        sessionKey = session.sessionKey
                        salt = genSalt(16)
                        createTime = Clock.System.now().epochSeconds
                        lastLogin = createTime
                    }
                } else {
                    user.lastLogin = Clock.System.now().epochSeconds
                    user.sessionKey = session.sessionKey
                    user
                }
                UserLoginResponse(data = UserLoginResponse.UserInfo(uu))
            }
            respond(rsq)
        }
    }
}

@Serializable
data class UserLoginRequest(var code: String)

@Serializable
data class UserLoginResponse(
    val errcode: Int = 0,
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
