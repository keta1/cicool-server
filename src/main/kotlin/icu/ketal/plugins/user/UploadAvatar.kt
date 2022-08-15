package icu.ketal.plugins.user

import icu.ketal.dao.User
import icu.ketal.data.ServiceError
import icu.ketal.utils.FILE_SIZE_LIMIT
import icu.ketal.utils.FILE_STORE_PATH
import icu.ketal.utils.catching
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
import kotlin.io.path.deleteIfExists
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.outputStream

context(UserRouting)
fun uploadAvatar() {
    routing.post("cicool/user/uploadAvatar") {
        call.catching {
            val header = call.request.headers
            val fileSize = header["Content-Length"]
            val userId = header["X-User-Id"]?.toInt()
            check(userId, request)
            if (fileSize == null) {
                throw ServiceError.FILE_SIZE_UNKNOWN
            }
            if (fileSize.toLong() > FILE_SIZE_LIMIT) {
                throw ServiceError.FILE_SIZE_LIMIT
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
            val avatarPic = transaction {
                val user = User.findById(userId!!)!!
                val avatarPic = user.avatarPic
                user.avatarPic = path.toString()
                Path(avatarPic)
            }
            withContext(Dispatchers.IO) {
                avatarPic.deleteIfExists()
            }
            call.respondError(ServiceError.OK)
        }
    }

    routing.post("cicool/user/getAvatar") {
        call.catching {
            val (userId) = call.receive<GetAvatarRequest>()
            check(userId, request)
            val path = transaction {
                User.findById(userId)!!.avatarPic
            }
            val file = Path(path)
            if (!file.exists()) {
                throw ServiceError.FILE_NOT_FOUND
            }
            call.respondFile(file.toFile())
        }
    }
}

@Serializable
data class GetAvatarRequest(var id: Int)
