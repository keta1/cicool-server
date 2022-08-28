package icu.ketal.plugins.word

import icu.ketal.dao.DailySum
import icu.ketal.dao.LearnRecord
import icu.ketal.dao.User
import icu.ketal.data.OFMatrix
import icu.ketal.data.ServiceError
import icu.ketal.serializers.TimeStampSerializer
import icu.ketal.table.DailySumDb
import icu.ketal.table.LearnRecordDb
import icu.ketal.utils.`Sm-5`
import icu.ketal.utils.catching
import icu.ketal.utils.decodeToDataClass
import icu.ketal.utils.encodeToJson
import icu.ketal.utils.now
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.time.Duration

context(WordRouting)
fun updateLearningRecord() {
    routing.post("cicool/word/updateLearningRecord") {
        call.catching {
            val (userId, learningRecord) = receive<UpdateLearningRecordReq>()
            icu.ketal.plugins.user.check(userId, request)
            transaction {
                val user = User.findById(userId)!!
                var ofMatrix = user.ofMatrix.decodeToDataClass<OFMatrix>()
                learningRecord.forEach {
                    val (OF, record) = `Sm-5`.sm_5(ofMatrix, it)
                    LearnRecord.find { LearnRecordDb.userId.eq(userId) and LearnRecordDb.wordId.eq(it.wordId) }
                        .first().apply {
                            this.lastToLearn = record.lastToLearn
                            this.nextToLearn = record.nextToLearn
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
            respond(ServiceError.OK)
        }
    }
}

@Serializable
data class UpdateLearningRecordReq(
    val userId: Int,
    val record: List<LearningRecord>
) {
    @Serializable
    data class LearningRecord(
        val wordId: Int,
        val EF: String,
        val quality: Int,
        val NOI: Int,
        val next_n: Int,
        @Serializable(with = TimeStampSerializer::class)
        val lastToLearn: Instant,
        @Serializable(with = TimeStampSerializer::class)
        val nextToLearn: Instant,
        val master: Boolean
    )
}
