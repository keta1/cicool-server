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
fun getBkUnlearnedWord() {
    routing.post("cicool/statistic/getBkUnlearnedWord") {
        call.catching {
            val (userId, bookId, num, skip) = receive<GetBkUnlearnedWordReq>()
            icu.ketal.plugins.user.check(userId, request)
            val rsq = transaction {
                val record = LearnRecord.find { LearnRecordDb.userId eq userId }.map { it.wordId }
                val words = WordInBook.find {
                    WordInBookDb.bookId eq bookId and
                            WordInBookDb.wordId.notInList(record)
                }.limit(num, skip).mapNotNull {
                    Word.findById(it.wordId)
                }.map { GetBkUnlearnedWordRsq.SWord(it) }
                GetBkUnlearnedWordRsq(words = words)
            }
            respond(rsq)
        }
    }
}

@Serializable
data class GetBkUnlearnedWordReq(
    val userId: Int,
    val bookId: Int,
    val num: Int = 20,
    val skip: Long = 0
)

@Serializable
data class GetBkUnlearnedWordRsq(
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
