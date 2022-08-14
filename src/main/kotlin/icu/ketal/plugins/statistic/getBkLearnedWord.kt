package icu.ketal.plugins.statistic

import icu.ketal.dao.LearnRecord
import icu.ketal.dao.Word
import icu.ketal.dao.WordInBook
import icu.ketal.data.ServiceError
import icu.ketal.table.LearnRecordDb
import icu.ketal.table.WordInBookDb
import icu.ketal.utils.logger
import icu.ketal.utils.respondError
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

context(StatisticRouting)
fun getBkLearnedWord() {
    routing.post("cicool/statistic/getBkLearnedWord") {
        kotlin.runCatching {
            val (userId, bookId, num, skip) = call.receive<GetBkLearnedWordReq>()
            val rsq = transaction {
                val wordInBook = WordInBook.find { WordInBookDb.bookId eq bookId }.map { it.wordId }
                val words = LearnRecord.find {
                    LearnRecordDb.userId.eq(userId) and
                            LearnRecordDb.wordId.inList(wordInBook)
                }
                    .limit(num, skip).mapNotNull {
                        Word.findById(it.wordId)
                    }.map { GetBkLearnedWordRsq.SWord(it) }
                GetBkLearnedWordRsq(words = words)
            }
            call.respond(rsq)
        }.onFailure {
            logger.warn(it.stackTraceToString())
            call.respondError(ServiceError.INTERNAL_SERVER_ERROR)
        }
    }
}

@Serializable
data class GetBkLearnedWordReq(
    val userId: Int,
    val bookId: Int,
    val num: Int,
    val skip: Long
)


@Serializable
data class GetBkLearnedWordRsq(
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
