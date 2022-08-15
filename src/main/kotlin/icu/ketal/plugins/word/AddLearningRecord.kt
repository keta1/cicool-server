package icu.ketal.plugins.word

import icu.ketal.dao.DailySum
import icu.ketal.dao.LearnRecord
import icu.ketal.data.ServiceError
import icu.ketal.table.DailySumDb
import icu.ketal.utils.catching
import icu.ketal.utils.now
import icu.ketal.utils.timeZone
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit.Companion.DAY
import kotlinx.datetime.Instant
import kotlinx.datetime.plus
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.time.Duration

context(WordRouting)
fun addLearningRecord() {
    routing.post("cicool/word/addLearningRecord") {
        call.catching {
            val (userId, record) = receive<AddLearningRecordReq>()
            icu.ketal.plugins.user.check(userId, request)
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
            respond(ServiceError.OK)
        }
    }
}

@Serializable
data class AddLearningRecordReq(
    val userId: Int,
    val record: List<LearningRecord>,
) {
    @Serializable
    data class LearningRecord(
        val wordId: Int,
        val lastToLearn: Instant = Clock.System.now(),
        val nextToLearn: Instant = lastToLearn.plus(1, DAY, timeZone),
        val EF: String = "2.5",
        val NOI: Int = 1,
        val next_n: Int = 0,
        val completed: Boolean = false,
        val repeatTimes: Int = 0,
        val master: Boolean = false,
        val createTime: Instant = lastToLearn
    )
}
