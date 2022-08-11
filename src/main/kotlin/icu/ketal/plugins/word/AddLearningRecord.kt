package icu.ketal.plugins.word

import icu.ketal.dao.DailySum
import icu.ketal.dao.LearnRecord
import icu.ketal.data.ServiceError
import icu.ketal.table.DailySumDb
import icu.ketal.utils.logger
import icu.ketal.utils.now
import icu.ketal.utils.respondError
import icu.ketal.utils.timeZone
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit.Companion.DAY
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.time.Duration

context(WordRouting)
fun addLearningRecord() {
    routing.post("cicool/word/addLearningRecord") {
        kotlin.runCatching {
            val (userId, record) = call.receive<AddLearningRecordReq>()
            transaction {
                record.forEach {
                    LearnRecord.new {
                        this.userId = userId
                        this.wordId = it.wordId
                        this.EF = it.EF
                        this.NOI = it.NOI
                        this.lastToLearn = it.lastToLearn
                        this.nextToLearn = it.nextToLearn
                        this.next_n = it.next_n
                        this.completed = it.completed
                        this.repeatTimes = it.repeatTimes
                        this.master = it.master
                        this.createTime = it.createTime
                    }
                }
                val sum = DailySum.find { DailySumDb.date eq Clock.System.now.date }.firstOrNull()
                if (sum == null) {
                    DailySum.new {
                        this.userId = userId
                        this.date = Clock.System.now.date
                        this.learn = record.size
                        this.review = 0
                        this.learnTime = Duration.ZERO
                    }
                } else {
                    sum.learn += record.size
                }
            }
            call.respond(ServiceError.OK)
        }.onFailure {
            logger.warn(it.stackTraceToString())
            call.respondError(ServiceError.INTERNAL_SERVER_ERROR)
        }
    }
}

@Serializable
data class AddLearningRecordReq(
    @SerialName("user_id")
    val userId: Int,
    val record: List<LearningRecord>,
) {
    @Serializable
    data class LearningRecord(
        val wordId: Int,
        val lastToLearn: LocalDateTime = Clock.System.now,
        val nextToLearn: LocalDateTime = lastToLearn.toInstant(timeZone)
            .plus(1, DAY, timeZone).toLocalDateTime(timeZone),
        val EF: String = "2.5",
        val NOI: Int = 1,
        val next_n: Int = 0,
        val completed: Boolean = false,
        val repeatTimes: Int = 0,
        val master: Boolean = false,
        val createTime: LocalDateTime = lastToLearn
    )
}
