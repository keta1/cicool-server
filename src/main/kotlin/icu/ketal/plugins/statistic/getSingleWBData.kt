package icu.ketal.plugins.statistic

import icu.ketal.dao.WordBook
import icu.ketal.data.ServiceError
import icu.ketal.utils.logger
import icu.ketal.utils.respondError
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction

context(StatisticRouting)
fun getSingleWBData() {
    routing.post("cicool/statistic/getSingleWBData") {
        kotlin.runCatching {
            val (userId, bookId) = call.receive<GetSingleWBDataReq>()
            val rsq = transaction {
                val book = WordBook.findById(bookId)!!
                GetSingleWBDataRsq(
                    errcode = 200,
                    book = GetSingleWBDataRsq.Book(book)
                )
            }
            call.respond(rsq)
        }.onFailure {
            logger.warn(it.stackTraceToString())
            call.respondError(ServiceError.INTERNAL_SERVER_ERROR)
        }
    }
}


@Serializable
data class GetSingleWBDataReq(
    val userId: Int,
    val bookId: Int,
)

@Serializable
data class GetSingleWBDataRsq(
    val errcode: Int = 0,
    val errmsg: String? = null,
    val book: Book
) {
    @Serializable
    data class Book(
        val name: String,
        val description: String,
        val total: Int,
        val coverType: String,
        val color: String?,
        val coverUrl: String?
    ) {
        constructor(book: WordBook) : this(
            book.name,
            book.description,
            book.total,
            book.coverType,
            book.color,
            book.coverUrl
        )
    }
}
