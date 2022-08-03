package icu.ketal.plugins.user

import icu.ketal.dao.User
import icu.ketal.data.ServiceError
import icu.ketal.utils.DBUtils
import icu.ketal.utils.FILE_SIZE_LIMIT
import icu.ketal.utils.FILE_STORE_PATH
import icu.ketal.utils.logger
import icu.ketal.utils.respondError
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.request.receiveStream
import io.ktor.server.response.respondFile
import io.ktor.server.routing.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.outputStream

context(UserRouting)
fun uploadAvatar() {
    routing.post("cicool/user/uploadAvatar") {
        kotlin.runCatching {
            val header = call.request.headers
            val fileSize = header["Content-Length"]
            val id = header["X-User-Id"]?.toInt()
            val cookie = call.request.cookies["TOKEN"]
            check(id, cookie)?.let {
                call.respondError(it)
                return@runCatching
            }
            if (fileSize == null) {
                call.respondError(ServiceError.FILE_SIZE_UNKNOWN)
                return@runCatching
            }
            if (fileSize.toLong() > FILE_SIZE_LIMIT) {
                call.respondError(ServiceError.FILE_SIZE_LIMIT)
                return@runCatching
            }
            val path = withContext(Dispatchers.IO) {
                val stream = call.receiveStream()
                val uuid = UUID.randomUUID().toString()
                val path = Path(FILE_STORE_PATH) / "$uuid.jpg"
                stream.use {
                    path.parent.createDirectories()
                    it.copyTo(path.outputStream())
                }

                path
            }
            transaction(DBUtils.db) {
                val user = User.findById(id!!)!!
                user.avatarPic = path.toString()
            }
            call.respondError(ServiceError.OK)
        }.onFailure {
            logger.warn(it.stackTraceToString())
            call.respondError(ServiceError.INTERNAL_SERVER_ERROR)
        }
    }

    routing.post("cicool/user/getAvatar") {
        kotlin.runCatching {
            val req = call.receive<GetAvatarRequest>()
            val cookie = call.request.cookies["TOKEN"]
            check(req.id, cookie)?.let {
                call.respondError(it)
                return@runCatching
            }
            val path = transaction {
                User.findById(req.id)!!.avatarPic
            }
            val file = Path(path)
            if (!file.exists()) {
                call.respondError(ServiceError.FILE_NOT_FOUND)
                return@runCatching
            }
            call.respondFile(file.toFile())
        }.onFailure {
            logger.warn(it.stackTraceToString())
            call.respondError(ServiceError.INTERNAL_SERVER_ERROR)
        }
    }
}

context(UserRouting)
@Serializable
data class GetAvatarRequest(
    var id: Int = -1
)
