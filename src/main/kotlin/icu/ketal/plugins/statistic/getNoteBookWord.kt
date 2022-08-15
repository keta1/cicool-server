package icu.ketal.plugins.statistic

import icu.ketal.dao.NoteBook
import icu.ketal.dao.Word
import icu.ketal.table.NoteBookDb
import icu.ketal.utils.catching
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction

context(StatisticRouting)
fun getNoteBookWord() {
    routing.post("cicool/statistic/getNoteBookWord") {
        call.catching {
            val (userId, num, skip) = receive<GetNoteBookWordReq>()
            icu.ketal.plugins.user.check(userId, request)
            val rsq = transaction {
                val words = NoteBook.find { NoteBookDb.userId eq userId }
                    .limit(num, skip).mapNotNull {
                        Word.findById(it.wordId)
                    }.map { GetNoteBookWordRsq.SWord(it) }
                GetNoteBookWordRsq(words = words)
            }
            respond(rsq)
        }
    }
}

@Serializable
data class GetNoteBookWordReq(
    val userId: Int,
    val num: Int = 20,
    val skip: Long = 0
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
