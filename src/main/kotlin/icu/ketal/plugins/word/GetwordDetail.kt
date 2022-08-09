package icu.ketal.plugins.word

import icu.ketal.dao.NoteBook
import icu.ketal.dao.Word
import icu.ketal.dao.WordBook
import icu.ketal.dao.WordInBook
import icu.ketal.data.ServiceError
import icu.ketal.plugins.user.check
import icu.ketal.table.NoteBookDb
import icu.ketal.table.WordInBookDb
import icu.ketal.utils.logger
import icu.ketal.utils.respondError
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction

context(WordRouting)
fun getWordDetail() {
    routing.post("cicool/word/getWordDetail") {
        kotlin.runCatching {
            val (userId, wordId) = call.receive<GetWordDetailReq>()
            val cookie = call.request.cookies["TOKEN"]
            check(userId, cookie)?.let {
                call.respondError(it)
                return@runCatching
            }
            val rsp = transaction {
                val word = Word.findById(wordId)!!
                val inNoteBook = NoteBook.find { NoteBookDb.wordId eq wordId }.any()
                val books = WordInBook.find { WordInBookDb.wordId eq wordId }.mapNotNull {
                    WordBook.findById(it.bookId)
                }.map { GetWordDetailRsp.SWordBook(it) }
                GetWordDetailRsp(
                    GetWordDetailRsp.SWord(word), inNoteBook, books
                )
            }
            call.respond(rsp)
        }.onFailure {
            logger.warn(it.stackTraceToString())
            call.respondError(ServiceError.INTERNAL_SERVER_ERROR)
        }
    }
}

@Serializable
data class GetWordDetailReq(
    val userId: Int,
    val wordId: Int
)

@Serializable
data class GetWordDetailRsp(
    val word: SWord,
    @SerialName("in_notebook")
    val inNoteBook: Boolean,
    val tagList: List<SWordBook> = emptyList()
) {
    @Serializable
    data class SWord(
        val id: Int,
        val word: String,
        val phonetic: String?,
        val definition: String?,
        val pos: String?,
        val collins: Int,
        val bnc: Int?,
        val frq: Int?,
        val exchange: String?,
    ) {
        constructor(word: Word) : this(
            word.id.value,
            word.word,
            word.phonetic,
            word.definition,
            word.pos,
            word.collins,
            word.bnc,
            word.frq,
            word.exchange
        )
    }

    @Serializable
    data class SWordBook(
        val id: Int,
        val name: String,
        val description: String?,
        val total: Int
    ) {
        constructor(book: WordBook) : this(
            book.id.value,
            book.name,
            book.description,
            book.total
        )
    }
}
