package icu.ketal.plugins.user

import icu.ketal.dao.User
import icu.ketal.data.ServiceError
import icu.ketal.utils.FILE_SIZE_LIMIT
import icu.ketal.utils.FILE_STORE_PATH
import icu.ketal.utils.catching
import icu.ketal.utils.respondError
import io.ktor.server.application.call
import io.ktor.server.http.content.files
import io.ktor.server.http.content.static
import io.ktor.server.http.content.staticRootFolder
import io.ktor.server.request.receiveStream
import io.ktor.server.routing.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.div
import kotlin.io.path.outputStream

context(UserRouting)
fun uploadAvatar() {
    routing.post("cicool/user/uploadAvatar") {
        call.catching {
            val header = request.headers
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
            respondError(ServiceError.OK)
        }
    }

    routing.static(FILE_STORE_PATH) {
        staticRootFolder = File(FILE_STORE_PATH)
        files(".")
    }
}
