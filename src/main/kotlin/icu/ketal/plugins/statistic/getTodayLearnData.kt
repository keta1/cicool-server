package icu.ketal.plugins.statistic

import icu.ketal.dao.DailySum
import icu.ketal.data.ServiceError
import icu.ketal.table.DailySumDb
import icu.ketal.utils.DurationSerializer
import icu.ketal.utils.logger
import icu.ketal.utils.now
import icu.ketal.utils.respondError
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.time.Duration

context(StatisticRouting)
fun getTodayLearnData() {
    routing.post("cicool/statistic/getTodayLearnData") {
        kotlin.runCatching {
            val (userId) = call.receive<GetTodayLearnDataReq>()
            val rsq = transaction {
                val sum = DailySum.find { DailySumDb.userId.eq(userId) and DailySumDb.date.eq(Clock.System.now.date) }
                    .firstOrNull()?.let { GetTodayLearnDataRsq.Data(it) } ?: GetTodayLearnDataRsq.Data()
                GetTodayLearnDataRsq(
                    errcode = 200,
                    data = sum
                )
            }
            call.respond(rsq)
        }.onFailure {
            logger.warn(it.stackTraceToString())
            call.respondError(ServiceError.INTERNAL_SERVER_ERROR)
        }
    }
}

@Serializable
data class GetTodayLearnDataReq(
    val userId: Int
)

@Serializable
data class GetTodayLearnDataRsq(
    val errcode: Int = 0,
    val errmsg: String? = null,
    val data: Data
) {
    @Serializable
    data class Data(
        @Serializable(with = DurationSerializer::class)
        val learnTime: Duration = Duration.ZERO,
        val learn: Int = 0,
        val review: Int = 0
    ) {
        constructor(sum: DailySum) : this(
            learnTime = sum.learnTime,
            learn = sum.learn,
            review = sum.review
        )
    }
}
