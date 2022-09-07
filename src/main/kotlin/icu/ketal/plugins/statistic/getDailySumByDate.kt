package icu.ketal.plugins.statistic

import icu.ketal.dao.DailySum
import icu.ketal.data.ServiceError
import icu.ketal.serializers.TimeStampSerializer
import icu.ketal.table.DailySumDb
import icu.ketal.utils.localDate
import icu.ketal.utils.logger
import icu.ketal.utils.respondError
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

context(StatisticRouting)
fun getDailySumByDate() {
    routing.post("cicool/statistic/getDailySumByDate") {
        call.runCatching {
            val (userId, _startDate, _endDate) = receive<GetDailySumByDateReq>()
            val rsq = transaction {
                val now = Clock.System.now()
                logger.info("mow is ${now.epochSeconds}")
                if (_startDate > _endDate || _startDate > now || _endDate > now) {
                    throw ServiceError.BAD_REQUEST
                }
                val (startDate, endDate) = Pair(_startDate.localDate, _endDate.localDate)
                val dailySum =
                    DailySum.find { DailySumDb.userId.eq(userId) and DailySumDb.date.between(startDate, endDate) }
                        .map { GetDailySumByDateRsq.Data(it) }
                GetDailySumByDateRsq(dailySum = dailySum)
            }
            call.respond(rsq)
        }.onFailure {
            logger.warn(it.stackTraceToString())
            call.respondError(ServiceError.INTERNAL_SERVER_ERROR)
        }
    }
}

@Serializable
data class GetDailySumByDateReq(
    val userId: Int,
    @Serializable(with = TimeStampSerializer::class)
    val startDate: Instant,
    @Serializable(with = TimeStampSerializer::class)
    val endDate: Instant = Clock.System.now(),
)

@Serializable
data class GetDailySumByDateRsq(
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
