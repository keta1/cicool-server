package icu.ketal.plugins

import icu.ketal.dao.User
import icu.ketal.data.webErr
import icu.ketal.table.UserDb
import icu.ketal.utils.TimeUtil
import icu.ketal.utils.WechatUtils
import icu.ketal.utils.logger
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
                val req = call.receive<UserRegisterRequest>()
                val session = WechatUtils.code2Session(req.code)
                transaction {
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
                    }
                }
                call.respond(session)
            }.onFailure {
                logger.warn(it.stackTraceToString())
                call.respond(webErr())
            }
        }
    }
}

@Serializable
data class UserRegisterRequest(
    val code: String,
    val nickName: String,
    val avatarUrl: String
)
