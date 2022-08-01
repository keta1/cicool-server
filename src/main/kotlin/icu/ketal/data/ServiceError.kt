package icu.ketal.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class ServiceError(
    @Transient
    var httpStatusCode: Int = 200,
    var errcode: Int = 0,
    var errmsg: String
) {
    companion object {
        val OK = ServiceError(200, 0, "OK")
        val BAD_REQUEST = ServiceError(400, 1, "Bad Request")
        val USER_NOTFOUND = ServiceError(400, 2, "User Not Found")
        val UNAUTHORIZED = ServiceError(401, 3, "Unauthorized")
        val FORBIDDEN = ServiceError(403, 4, "Forbidden")
        val NOT_FOUND = ServiceError(404, 5, "Not Found")
        val FILE_SIZE_UNKNOWN = ServiceError(400, 5, "File Size Unknown")
        val FILE_SIZE_LIMIT = ServiceError(400, 5, "File Size Is Too Large")
        val INTERNAL_SERVER_ERROR = ServiceError(500, 500, "Internal Server Error")
    }
}
