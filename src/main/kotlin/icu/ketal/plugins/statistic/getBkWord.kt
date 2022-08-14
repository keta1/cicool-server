package icu.ketal.plugins.statistic

import icu.ketal.dao.Word
import icu.ketal.dao.WordInBook
import icu.ketal.data.ServiceError
import icu.ketal.table.WordInBookDb
import icu.ketal.utils.logger
import icu.ketal.utils.respondError
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction

context(StatisticRouting)
fun getBkWord() {
    routing.post("cicool/statistic/getBkWord") {
        kotlin.runCatching {
            val (userId, bookId, num, skip) = call.receive<GetBkWordReq>()
            val rsq = transaction {
                val words = WordInBook.find {
                    WordInBookDb.bookId eq bookId
                }.limit(num, skip).mapNotNull {
                    Word.findById(it.wordId)
                }.map { GetBkWordRsq.SWord(it) }
                GetBkWordRsq(words = words)
            }
            call.respond(rsq)
        }.onFailure {
            logger.warn(it.stackTraceToString())
            call.respondError(ServiceError.INTERNAL_SERVER_ERROR)
        }
    }
}

@Serializable
data class GetBkWordReq(
    val userId: Int,
    val bookId: Int,
    val num: Int = 20,
    val skip: Long = 0
)

@Serializable
data class GetBkWordRsq(
    val errcode: Int = 200,
    val errmsg: String? = null,
    val words: List<SWord>
) {
    @Serializable
    data class SWord(
        val wordId: Int,
        val word: String,
        val translation: String?
    ) {
        constructor(word: Word) : this(
            word.id.value,
            word.word,
            word.translation
        )
    }
}