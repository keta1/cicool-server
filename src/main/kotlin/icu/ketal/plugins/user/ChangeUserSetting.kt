package icu.ketal.plugins.user

import icu.ketal.dao.User
import icu.ketal.data.ServiceError
import icu.ketal.utils.catching
import icu.ketal.utils.logger
import icu.ketal.utils.respondError
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import org.jetbrains.exposed.sql.transactions.transaction

context(UserRouting)
fun changeUserSetting() {
    routing.post("cicool/user/changeUserSetting") {
        call.catching {
            val (id, settings) = receive<ChangeUserSettingRequest>()
            check(id, request)
            logger.info(settings.toString())
            transaction {
                val user = User.findById(id)!!
                user.settings = settings.toString()
            }
            respondError(ServiceError.OK)
        }
    }
}

@Serializable
data class ChangeUserSettingRequest(
    var userId: Int,
    var settings: JsonObject
)
