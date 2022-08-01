package icu.ketal.plugins.user

import icu.ketal.data.ServiceError
import icu.ketal.utils.FILE_SIZE_LIMIT
import icu.ketal.utils.FILE_STORE_PATH
import icu.ketal.utils.logger
import icu.ketal.utils.respondError
import io.ktor.server.application.call
import io.ktor.server.request.receiveStream
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.div
import kotlin.io.path.outputStream

context(UserRouting)
fun uploadAvatar() {
    routing.post("cicool/user/uploadAvatar") {
        kotlin.runCatching {
            val fileSize = call.request.headers["Content-Length"]
            if (fileSize == null) {
                call.respondError(ServiceError.FILE_SIZE_UNKNOWN)
                return@runCatching
            }
            if (fileSize.toLong() > FILE_SIZE_LIMIT) {
                call.respondError(ServiceError.FILE_SIZE_LIMIT)
                return@runCatching
            }
            val id = withContext(Dispatchers.IO) {
                val stream = call.receiveStream()
                val id = UUID.randomUUID().toString()
                val path = Path(FILE_STORE_PATH) / "${UUID.randomUUID()}.jpg"
                stream.use {
                    path.parent.createDirectories()
                    it.copyTo(path.outputStream())
                }
                id
            }
            call.respond(
                UploadAvatarResponse(id = id)
            )
        }.onFailure {
            logger.warn(it.stackTraceToString())
            call.respondError(ServiceError.INTERNAL_SERVER_ERROR)
        }
    }
}

context(UserRouting)
@Serializable
data class UploadAvatarResponse(
    var errcode: Int = 0,
    var errmsg: String = "OK",
    var id: String
)
