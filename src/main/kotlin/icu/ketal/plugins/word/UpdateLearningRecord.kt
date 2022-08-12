package icu.ketal.plugins.word

import icu.ketal.dao.DailySum
import icu.ketal.dao.LearnRecord
import icu.ketal.dao.User
import icu.ketal.data.OFMatrix
import icu.ketal.data.ServiceError
import icu.ketal.table.DailySumDb
import icu.ketal.table.LearnRecordDb
import icu.ketal.utils.`Sm-5`
import icu.ketal.utils.decodeToDataClass
import icu.ketal.utils.encodeToJson
import icu.ketal.utils.logger
import icu.ketal.utils.now
import icu.ketal.utils.respondError
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.time.Duration

context(WordRouting)
fun updateLearningRecord() {
    routing.post("cicool/word/updateLearningRecord") {
        kotlin.runCatching {
            val (userId, learningRecord) = call.receive<UpdateLearningRecordReq>()
            transaction {
                val user = User.findById(userId)!!
                var ofMatrix = user.ofMatrix.decodeToDataClass<OFMatrix>()
                learningRecord.forEach {
                    val (OF, record) = `Sm-5`.sm_5(ofMatrix, it)
                    LearnRecord.find { LearnRecordDb.userId.eq(userId) and LearnRecordDb.wordId.eq(it.word_id) }
                        .first().apply {
                            this.lastToLearn = record.last_l
                            this.nextToLearn = record.next_l!!
                            this.NOI = record.NOI
                            this.next_n = record.next_n
                            this.master = record.master
                        }
                    ofMatrix = OF
                }

                val sum = DailySum.find { DailySumDb.date eq Clock.System.now.date }.firstOrNull()
                if (sum == null) {
                    DailySum.new {
                        this.userId = userId
                        this.date = Clock.System.now.date
                        this.learn = 0
                        this.review = learningRecord.size
                        this.learnTime = Duration.ZERO
                    }
                } else {
                    sum.review += learningRecord.size
                }

                user.ofMatrix = ofMatrix.encodeToJson()
            }
            call.respond(ServiceError.OK)
        }.onFailure {
            logger.warn(it.stackTraceToString())
            call.respondError(ServiceError.INTERNAL_SERVER_ERROR)
        }
    }
}

@Serializable
data class UpdateLearningRecordReq(
    @SerialName("user_id")
    val userId: Int,
    val wordLearningRecord: List<LearningRecord>
) {
    @Serializable
    data class LearningRecord(
        val word_id: Int,
        val EF: String,
        val quality: Int,
        val NOI: Int,
        val next_n: Int,
        val last_l: Instant,
        val next_l: Instant? = null,
        val master: Boolean
    )
}
