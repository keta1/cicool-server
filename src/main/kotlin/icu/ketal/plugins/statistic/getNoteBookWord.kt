package icu.ketal.plugins.statistic

import icu.ketal.dao.NoteBook
import icu.ketal.dao.Word
import icu.ketal.data.ServiceError
import icu.ketal.table.NoteBookDb
import icu.ketal.utils.logger
import icu.ketal.utils.respondError
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction

context(StatisticRouting)
fun getNoteBookWord() {
    routing.post("cicool/statistic/getNoteBookWord") {
        kotlin.runCatching {
            val (userId, num, skip) = call.receive<GetNoteBookWordReq>()
            val rsq = transaction {
                val words = NoteBook.find { NoteBookDb.userId eq userId }
                    .limit(num, skip).mapNotNull {
                        Word.findById(it.wordId)
                    }.map { GetNoteBookWordRsq.SWord(it) }
                GetNoteBookWordRsq(words = words)
            }
            call.respond(rsq)
        }.onFailure {
            logger.warn(it.stackTraceToString())
            call.respondError(ServiceError.INTERNAL_SERVER_ERROR)
        }
    }
}

@Serializable
data class GetNoteBookWordReq(
    val userId: Int,
    val num: Int,
    val skip: Long
)

@Serializable
data class GetNoteBookWordRsq(
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
