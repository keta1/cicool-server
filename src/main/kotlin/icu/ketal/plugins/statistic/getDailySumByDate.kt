package icu.ketal.plugins.statistic

import icu.ketal.dao.DailySum
import icu.ketal.data.ServiceError
import icu.ketal.serializers.TimeStampSerializer
import icu.ketal.table.DailySumDb
import icu.ketal.utils.LocalDateProgression
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
                if (_startDate > _endDate || _startDate > now || _endDate > now) {
                    throw ServiceError.BAD_REQUEST
                }
                val (startDate, endDate) = Pair(_startDate.localDate, _endDate.localDate)
                val dailySum =
                    DailySum.find { DailySumDb.userId.eq(userId) and DailySumDb.date.between(startDate, endDate) }
                val allSum = LocalDateProgression(startDate, endDate).map { date ->
                    val sum = dailySum.find { it.date == date }
                    GetDailySumByDateRsq.Data(date, sum?.learn ?: 0, sum?.review ?: 0)
                }
                GetDailySumByDateRsq(dailySum = allSum)
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
    )
}
