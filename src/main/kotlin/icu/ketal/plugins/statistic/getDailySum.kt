package icu.ketal.plugins.statistic

import icu.ketal.dao.DailySum
import icu.ketal.data.ServiceError
import icu.ketal.table.DailySumDb
import icu.ketal.utils.logger
import icu.ketal.utils.now
import icu.ketal.utils.respondError
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

context(StatisticRouting)
fun getDailySum() {
    routing.post("cicool/statistic/getDailySum") {
        kotlin.runCatching {
            val (userId, skip) = call.receive<GetDailySumReq>()
            val rsq = transaction {
                val dailySum =
                    DailySum.find { DailySumDb.userId.eq(userId) and DailySumDb.date.lessEq(Clock.System.now.date) }
                        .limit(10, skip)
                        .map { GetDailySumRsq.Data(it) }

                GetDailySumRsq(
                    errcode = 200,
                    dailySum = dailySum
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
data class GetDailySumReq(
    val userId: Int,
    val skip: Long = 0
)

@Serializable
data class GetDailySumRsq(
    val errcode: Int = 0,
    val errmsg: String? = null,
    val dailySum: List<Data>
) {
    @Serializable
    data class Data(
        val date: LocalDate,
        val learn: Int = 0,
        val review: Int = 0
    ) {
        constructor(sum: DailySum) : this(
            date = sum.date,
            learn = sum.learn,
            review = sum.review
        )
    }
}
