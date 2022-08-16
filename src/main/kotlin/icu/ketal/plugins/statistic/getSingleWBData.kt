package icu.ketal.plugins.statistic

import icu.ketal.dao.WordBook
import icu.ketal.utils.catching
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction

context(StatisticRouting)
fun getSingleWBData() {
    routing.post("cicool/statistic/getSingleWBData") {
        call.catching {
            val (userId, bookId) = call.receive<GetSingleWBDataReq>()
            icu.ketal.plugins.user.check(userId, request)
            val rsq = transaction {
                val book = WordBook.findById(bookId)!!
                GetSingleWBDataRsq(book = GetSingleWBDataRsq.Book(book))
            }
            respond(rsq)
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
