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
fun getAllWBData() {
    routing.post("cicool/statistic/getAllWBData") {
        call.catching {
            val (userId) = receive<GetAllWBDataReq>()
            icu.ketal.plugins.user.check(userId, request)
            val rsq = transaction {
                val books = WordBook.all().map { GetAllWBDataRsq.Book(it) }
                GetAllWBDataRsq(books = books)
            }
            respond(rsq)
        }
    }
}


@Serializable
data class GetAllWBDataReq(
    val userId: Int
)

@Serializable
data class GetAllWBDataRsq(
    val errcode: Int = 0,
    val errmsg: String? = null,
    val books: List<Book>
) {
    @Serializable
    data class Book(
        val bookId: Int,
        val name: String,
        val description: String,
        val total: Int,
        val coverType: String,
        val color: String?,
        val coverUrl: String?
    ) {
        constructor(book: WordBook) : this(
            book.id.value,
            book.name,
            book.description,
            book.total,
            book.coverType,
            book.color,
            book.coverUrl
        )
    }
}
