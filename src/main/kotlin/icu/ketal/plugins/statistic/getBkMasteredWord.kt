package icu.ketal.plugins.statistic

import icu.ketal.dao.LearnRecord
import icu.ketal.dao.Word
import icu.ketal.dao.WordInBook
import icu.ketal.table.LearnRecordDb
import icu.ketal.table.WordInBookDb
import icu.ketal.utils.catching
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

context(StatisticRouting)
fun getBkMasteredWord() {
    routing.post("cicool/statistic/getBkMasteredWord") {
        call.catching {
            val (userId, bookId, num, skip) = receive<GetBkMasteredWordReq>()
            icu.ketal.plugins.user.check(userId, request)
            val rsq = transaction {
                val wordInBook = WordInBook.find { WordInBookDb.bookId eq bookId }.map { it.wordId }
                val words = LearnRecord.find {
                    LearnRecordDb.userId.eq(userId) and
                            LearnRecordDb.master and
                            LearnRecordDb.wordId.inList(wordInBook)
                }.limit(num, skip).mapNotNull {
                    Word.findById(it.wordId)
                }.map { GetBkMasteredWordRsq.SWord(it) }
                GetBkMasteredWordRsq(words = words)
            }
            respond(rsq)
        }
    }
}

@Serializable
data class GetBkMasteredWordReq(
    val userId: Int,
    val bookId: Int,
    val size: Int = 20,
    val skip: Long = 0
)

@Serializable
data class GetBkMasteredWordRsq(
    val errcode: Int = 0,
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
