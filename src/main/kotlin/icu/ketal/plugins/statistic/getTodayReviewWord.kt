package icu.ketal.plugins.statistic

import icu.ketal.dao.LearnRecord
import icu.ketal.dao.Word
import icu.ketal.data.ServiceError
import icu.ketal.table.LearnRecordDb
import icu.ketal.utils.logger
import icu.ketal.utils.respondError
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
        kotlin.runCatching {
            val (userId, num, skip) = call.receive<GetTodayReviewWordReq>()
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
            call.respond(rsq)
        }.onFailure {
            logger.warn(it.stackTraceToString())
            call.respondError(ServiceError.INTERNAL_SERVER_ERROR)
        }
    }
}

@Serializable
data class GetTodayReviewWordReq(
    val userId: Int,
    val num: Int = 20,
    val skip: Long = 0
)

@Serializable
data class GetTodayReviewWordRsq(
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
