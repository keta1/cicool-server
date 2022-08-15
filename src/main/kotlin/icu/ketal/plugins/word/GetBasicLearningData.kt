package icu.ketal.plugins.word

import icu.ketal.dao.LearnRecord
import icu.ketal.dao.WordInBook
import icu.ketal.table.LearnRecordDb
import icu.ketal.table.WordInBookDb
import icu.ketal.utils.catching
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction

context(WordRouting)
fun getBasicLearningData() {
    routing.post("cicool/word/getBasicLearningData") {
        call.catching {
            val (userId, wordBookId) = receive<GetBasicLearningDataReq>()
            icu.ketal.plugins.user.check(userId, request)
            val rsp = transaction {
                val words = WordInBook.find { WordInBookDb.bookId eq wordBookId }.asSequence()
                val learnData = LearnRecord.find {
                    LearnRecordDb.userId.eq(userId)
                }.asSequence()
                    .filter { words.any { word -> word.wordId == it.wordId } }
                val total = words.count()
                val learned = learnData.count()
                val needToReview =
                    learnData.count { it.completed && !it.master && it.nextToLearn <= Clock.System.now() }
                GetBasicLearningDataRsq(
                    needToLearn = total - learned,
                    needToReview = needToReview
                )
            }
            respond(rsp)
        }
    }
}

@Serializable
data class GetBasicLearningDataReq(
    val userId: Int,
    val wordBookId: Int
)

@Serializable
data class GetBasicLearningDataRsq(
    val errcode: Int = 0,
    val errmsg: String? = null,
    val needToLearn: Int = -1,
    val needToReview: Int = -1
)
