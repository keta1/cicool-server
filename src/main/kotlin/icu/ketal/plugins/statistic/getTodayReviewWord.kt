package icu.ketal.plugins.statistic

import icu.ketal.dao.LearnRecord
import icu.ketal.dao.Word
import icu.ketal.table.LearnRecordDb
import icu.ketal.utils.catching
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.transactions.transaction

context(StatisticRouting)
fun getTodayReviewWord() {
    routing.post("cicool/statistic/getTodayReviewWord") {
        call.catching {
            val (userId, num, skip) = receive<GetTodayReviewWordReq>()
            icu.ketal.plugins.user.check(userId, request)
            val rsq = transaction {
                val words = LearnRecord.find {
                    LearnRecordDb.userId.eq(userId) and
                            LearnRecordDb.lastToLearn.date().eq(CurrentDateTime.date()) and
                            LearnRecordDb.createTime.date().neq(CurrentDateTime.date())
                }.limit(num, skip).mapNotNull {
                    Word.findById(it.wordId)
                }.map { GetTodayReviewWordRsq.SWord(it) }
                GetTodayReviewWordRsq(words = words)
            }
            respond(rsq)
        }
    }
}

@Serializable
data class GetTodayReviewWordReq(
    val userId: Int,
    val size: Int = 20,
    val skip: Long = 0
)

@Serializable
data class GetTodayReviewWordRsq(
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
