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
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Random
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

context(WordRouting)
fun getLearningData() {
    routing.post("cicool/word/getLearningData") {
        call.catching {
            val (userId, wordBookId, groupSize, sample) = receive<GetLearningDataReq>()
            icu.ketal.plugins.user.check(userId, request)
            val sampleSize = if (sample) 5 else 0
            val rsp = transaction {
                val learnData = LearnRecord.find {
                    LearnRecordDb.userId.eq(userId)
                }.toList()
                val noteBook = NoteBook.find { NoteBookDb.userId eq userId }.toList()
                val words = WordInBook.find {
                    WordInBookDb.bookId.eq(wordBookId) and
                            // filter words that have been learned
                            WordInBookDb.wordId.notInList(learnData.filter { it.completed }.map { it.wordId })
                }
                    .orderBy(WordInBookDb.bookId to SortOrder.DESC)
                    .limit(groupSize)
                    .asSequence()
                    .map { Word.findById(it.wordId)!! }
                    .mapIndexed { index, word ->
                        val learningRecord = learnData.filter { !it.completed }
                            .firstOrNull { it.wordId == word.id.value }
                            ?.let { GetLearningDataRsq.LearningRecord(it) }
                        GetLearningDataRsq.SWord(
                            index,
                            word,
                            noteBook.any { it.wordId == word.id.value },
                            learningRecord,
                            genSample(wordBookId, sampleSize)
                        )
                    }.toList()
                val wordIdList = List(words.size) { words[it].wordId }
                GetLearningDataRsq(
                    errcode = 200,
                    data = GetLearningDataRsq.Data(words, wordIdList)
                )
            }
            respond(rsp)
        }
    }
}

private fun genSample(wordBookId: Int, size: Int): List<GetLearningDataRsq.Sample> {
    return transaction {
        WordInBook.find { WordInBookDb.bookId eq wordBookId }
            .orderBy(Random() to SortOrder.DESC)
            .limit(size)
            .asSequence()
            .map { Word.findById(it.wordId)!! }
            .mapIndexed { index, word -> GetLearningDataRsq.Sample(index, word) }
            .toList()
    }
}

@Serializable
data class GetLearningDataReq(
    val userId: Int,
    val wordBookId: Int,
    val groupSize: Int = 10,
    val sample: Boolean = true
)

@Serializable
data class GetLearningDataRsq(
    val errcode: Int = 0,
    val errmsg: String? = null,
    val data: Data
) {
    @Serializable
    data class Data(
        @SerialName("word_list")
        val wordList: List<SWord>,
        val wordIdList: List<Int>
    )

    @Serializable
    data class SWord(
        val id: Int,
        val wordId: Int,
        val word: String,
        val translation: String?,
        val phonetic: String?,
        val inNotebook: Boolean,
        val learningRecord: LearningRecord?,
        val sampleList: List<Sample>
    ) {
        constructor(
            id: Int,
            word: Word,
            inNotebook: Boolean,
            learningRecord: LearningRecord?,
            sampleList: List<Sample>
        ) : this(
            id,
            word.id.value,
            word.word,
            word.translation,
            word.phonetic,
            inNotebook,
            learningRecord,
            sampleList
        )
    }

    @Serializable
    data class LearningRecord(
        val repeatTimes: Int,
        val learnTime: Instant
    ) {
        constructor(learnRecord: LearnRecord) : this(
            learnRecord.repeatTimes,
            learnRecord.lastToLearn
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
