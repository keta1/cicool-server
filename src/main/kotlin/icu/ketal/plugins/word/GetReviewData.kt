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
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Random
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

context(WordRouting)
fun getReviewData() {
    routing.post("cicool/word/getReviewData") {
        call.catching {
            val (userId, wordBookId, groupSize, sample) = receive<GetReviewDataReq>()
            icu.ketal.plugins.user.check(userId, request)
            val sampleSize = if (sample) 5 else 0
            val rsp = transaction {
                val wordInBook = WordInBook.find { WordInBookDb.bookId eq wordBookId }.toList()
                val learnData = LearnRecord.find {
                    LearnRecordDb.userId.eq(userId) and
                            LearnRecordDb.nextToLearn.lessEq(Clock.System.now())
                }.orderBy(LearnRecordDb.nextToLearn to SortOrder.DESC)
                    .limit(groupSize).asSequence()
                val noteBook = NoteBook.find { NoteBookDb.userId eq userId }
                val words = learnData.map { Word.findById(it.wordId)!! }
                    .filter { wordInBook.any { word -> word.wordId == it.id.value } }
                    .mapIndexed { index, word ->
                        GetReviewDataRsq.SWord(
                            index,
                            word,
                            noteBook.any { it.wordId == word.id.value },
                            GetReviewDataRsq.LearningRecord(learnData.first { it.wordId == word.id.value }),
                            genSample(wordBookId, sampleSize)
                        )
                    }.toList()
                GetReviewDataRsq(errcode = 200, data = GetReviewDataRsq.Data(words))
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
            .mapIndexed { index, word -> GetReviewDataRsq.Sample(index, word) }
            .toList()
    }
}

@Serializable
data class GetReviewDataReq(
    @SerialName("user_id")
    val userId: Int,
    @SerialName("wd_bk_id")
    val wordBookId: Int,
    val groupSize: Int = 10,
    val sample: Boolean = true
)

@Serializable
data class GetReviewDataRsq(
    val errcode: Int = 0,
    val errmsg: String? = null,
    val data: Data
) {
    @Serializable
    data class Data(
        val wordList: List<SWord>
    )

    @Serializable
    data class SWord(
        val id: Int,
        val wordId: Int,
        val word: String,
        val translation: String?,
        val phonetic: String?,
        val inNotebook: Boolean,
        val record: LearningRecord,
        val sampleList: List<Sample>
    ) {
        constructor(id: Int, word: Word, inNotebook: Boolean, record: LearningRecord, sampleList: List<Sample>) : this(
            id,
            word.id.value,
            word.word,
            word.translation,
            word.phonetic,
            inNotebook,
            record,
            sampleList
        )
    }

    @Serializable
    data class LearningRecord(
        val EF: String,
        val NOI: Int,
        val lastToLearn: Instant,
        val nextToLearn: Instant,
        val master: Boolean,
        val next_n: Int
    ) {
        constructor(learnRecord: LearnRecord) : this(
            learnRecord.EF,
            learnRecord.NOI,
            learnRecord.lastToLearn,
            learnRecord.nextToLearn,
            learnRecord.master,
            learnRecord.next_n
        )
    }

    @Serializable
    data class Sample(
        val id: Int,
        val wordId: Int,
        val word: String,
        val translation: String?
    ) {
        constructor(id: Int, word: Word) : this(
            id,
            word.id.value,
            word.word,
            word.translation
        )
    }
}
