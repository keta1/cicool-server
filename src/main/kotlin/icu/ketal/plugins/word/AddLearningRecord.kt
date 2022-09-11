package icu.ketal.plugins.word

import icu.ketal.dao.DailySum
import icu.ketal.dao.LearnRecord
import icu.ketal.data.ServiceError
import icu.ketal.serializers.TimeStampSerializer
import icu.ketal.table.DailySumDb
import icu.ketal.table.LearnRecordDb
import icu.ketal.utils.catching
import icu.ketal.utils.now
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.plus
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import kotlin.time.Duration

context(WordRouting)
fun addLearningRecord() {
    routing.post("cicool/word/addLearningRecord") {
        call.catching {
            val (userId, records) = receive<AddLearningRecordReq>()
            icu.ketal.plugins.user.check(userId, request)
            transaction {
                records.forEach { record ->
                    updateLearnRecord(userId, record)
                }
                updateDailySum(userId, records.size)
            }
            respond(ServiceError.OK)
        }
    }
}

fun updateLearnRecord(userId: Int, record: AddLearningRecordReq.LearningRecord) {
    val cache = LearnRecord.find { LearnRecordDb.wordId eq record.wordId }.firstOrNull()
    val now = Clock.System.now()
    if (cache == null) {
        LearnRecord.new {
            this.userId = userId
            this.wordId = record.wordId
            this.lastToLearn = record.lastToLearn
            this.nextToLearn = now.plus(reviewDate[0] * 24, DateTimeUnit.HOUR)
            this.learnTimes = 1
            this.completed = record.completed
            this.repeatTimes = record.repeatTimes
            this.master = record.master
            this.createTime = record.createTime
        }
    } else {
        val times = cache.learnTimes + 1
        LearnRecordDb.update({ LearnRecordDb.id eq cache.id }) {
            it[lastToLearn] = record.lastToLearn
            it[completed] = record.completed
            it[repeatTimes] = record.repeatTimes
            it[learnTimes] = times
            it[master] = record.master || times >= reviewDate.size
            if (times < reviewDate.size) {
                it[nextToLearn] = now.plus(reviewDate[times] * 24, DateTimeUnit.HOUR)
            }
            it[nextToLearn] = record.createTime
        }
    }
}

fun updateDailySum(userId: Int, count: Int) {
    val sum = DailySum.find { DailySumDb.date eq Clock.System.now.date }.firstOrNull()
    if (sum == null) {
        DailySum.new {
            this.userId = userId
            this.date = Clock.System.now.date
            this.learn = count
            this.review = 0
            this.learnTime = Duration.ZERO
        }
    } else {
        sum.learn += count
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
        @Serializable(with = TimeStampSerializer::class)
        val lastToLearn: Instant = Clock.System.now(),
        val completed: Boolean = false,
        val repeatTimes: Int = 0,
        val master: Boolean = false,
        @Serializable(with = TimeStampSerializer::class)
        val createTime: Instant = lastToLearn
    )
}
