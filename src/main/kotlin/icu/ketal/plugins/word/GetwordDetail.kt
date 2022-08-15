package icu.ketal.plugins.word

import icu.ketal.dao.NoteBook
import icu.ketal.dao.Word
import icu.ketal.dao.WordBook
import icu.ketal.dao.WordInBook
import icu.ketal.plugins.user.check
import icu.ketal.table.NoteBookDb
import icu.ketal.table.WordInBookDb
import icu.ketal.utils.catching
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction

context(WordRouting)
fun getWordDetail() {
    routing.post("cicool/word/getWordDetail") {
        call.catching {
            val (userId, wordId) = receive<GetWordDetailReq>()
            check(userId, request)
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
            respond(rsp)
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
    val inNoteBook: Boolean,
    val tagList: List<SWordBook> = emptyList()
) {
    @Serializable
    data class SWord(
        val id: Int,
        val word: String,
        val translation: String?,
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
            word.translation,
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
