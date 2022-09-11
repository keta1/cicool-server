package icu.ketal.plugins.word

import icu.ketal.dao.LearnRecord
import icu.ketal.dao.NoteBook
import icu.ketal.dao.Word
import icu.ketal.dao.WordInBook
import icu.ketal.table.LearnRecordDb
import icu.ketal.table.NoteBookDb
import icu.ketal.table.WordInBookDb
import icu.ketal.utils.catching
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Random
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

val reviewDate = intArrayOf(1, 1, 3, 7, 31)

context(WordRouting)
fun getReviewData() {
    routing.post("cicool/word/getReviewData") {
        call.catching {
            val (userId, wordBookId, size, sample) = receive<GetReviewDataReq>()
            icu.ketal.plugins.user.check(userId, request)
            val sampleSize = if (sample) 5 else 0
            val rsp = transaction {
                val wordInBook = WordInBook.find { WordInBookDb.bookId eq wordBookId }.toList()
                val learnData = LearnRecord.find {
                    LearnRecordDb.userId.eq(userId) and LearnRecordDb.nextToLearn.lessEq(Clock.System.now())
                }.orderBy(LearnRecordDb.nextToLearn to SortOrder.DESC)
                    .limit(size).asSequence()
                val noteBook = NoteBook.find { NoteBookDb.userId eq userId }
                val words = learnData.map { Word.findById(it.wordId)!! }
                    .filter { wordInBook.any { word -> word.wordId == it.id.value } }
                    .map { word ->
                        GetReviewDataRsq.SWord(
                            word,
                            noteBook.any { it.wordId == word.id.value },
                            genSample(wordBookId, sampleSize)
                        )
                    }.toList()
                GetReviewDataRsq(wordList = words)
            }
            respond(rsp)
        }
    }
}

private fun genSample(wordBookId: Int, size: Int): List<GetReviewDataRsq.Sample> {
    return transaction {
        WordInBook.find { WordInBookDb.bookId eq wordBookId }
            .orderBy(Random() to SortOrder.DESC)
            .limit(size)
            .asSequence()
            .map { Word.findById(it.wordId)!! }
            .map { word -> GetReviewDataRsq.Sample(word) }
            .toList()
    }
}

@Serializable
data class GetReviewDataReq(
    val userId: Int,
    val wordBookId: Int,
    val size: Int = 10,
    val sample: Boolean = true
)

@Serializable
data class GetReviewDataRsq(
    val errcode: Int = 0,
    val errmsg: String? = null,
    val wordList: List<SWord>
) {
    @Serializable
    data class SWord(
        val wordId: Int,
        val word: String,
        val translation: String?,
        val phonetic: String?,
        val inNotebook: Boolean,
        val sampleList: List<Sample>
    ) {
        constructor(word: Word, inNotebook: Boolean, sampleList: List<Sample>) : this(
            word.id.value,
            word.word,
            word.translation,
            word.phonetic,
            inNotebook,
            sampleList
        )
    }

    @Serializable
    data class Sample(
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
