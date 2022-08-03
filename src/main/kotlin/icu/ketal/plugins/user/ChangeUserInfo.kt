package icu.ketal.plugins.user

import icu.ketal.dao.User
import icu.ketal.data.ServiceError
import icu.ketal.utils.DBUtils
import icu.ketal.utils.logger
import icu.ketal.utils.respondError
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction

context(UserRouting)
fun changeUserInfo() {
    routing.post("cicool/user/changeUserInfo") {
        kotlin.runCatching {
            val req = call.receive<ChangeUserInfoRequest>()
            val cookie = call.request.cookies["TOKEN"]
            check(req.id, cookie)?.let {
                call.respondError(it)
                return@runCatching
            }
            transaction(DBUtils.db) {
                val user = User.findById(req.id)
                req.nickName?.let { user!!.nickName = it }
            }
            call.respondError(ServiceError.OK)
        }.onFailure {
            logger.warn(it.stackTraceToString())
            call.respondError(ServiceError.INTERNAL_SERVER_ERROR)
        }
    }
}

@Serializable
data class ChangeUserInfoRequest(
    var id: Int = -1,
    var nickName: String? = null
)
