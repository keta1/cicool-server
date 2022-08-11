package icu.ketal.plugins.word

import icu.ketal.dao.LearnRecord
import icu.ketal.dao.WordInBook
import icu.ketal.data.ServiceError
import icu.ketal.table.LearnRecordDb
import icu.ketal.table.WordInBookDb
import icu.ketal.utils.logger
import icu.ketal.utils.now
import icu.ketal.utils.respondError
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction

context(WordRouting)
fun getBasicLearningData() {
    routing.post("cicool/word/getBasicLearningData") {
        kotlin.runCatching {
            val (userId, wordBookId) = call.receive<GetBasicLearningDataReq>()
            val rsp = transaction {
                val words = WordInBook.find { WordInBookDb.bookId eq wordBookId }.asSequence()
                val learnData = LearnRecord.find {
                    LearnRecordDb.userId.eq(userId)
                }.asSequence()
                    .filter { words.any { word -> word.wordId == it.wordId } }
                val total = words.count()
                val learned = learnData.count()
                val needToReview = learnData.count { it.completed && !it.master && it.nextToLearn <= Clock.System.now }
                GetBasicLearningDataRsq(
                    needToLearn = total - learned,
                    needToReview = needToReview
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
data class GetBasicLearningDataReq(
    val userId: Int,
    @SerialName("wd_bk_id")
    val wordBookId: Int
)

@Serializable
data class GetBasicLearningDataRsq(
    val errcode: Int = 0,
    val errmsg: String? = null,
    val needToLearn: Int = -1,
    val needToReview: Int = -1
)
