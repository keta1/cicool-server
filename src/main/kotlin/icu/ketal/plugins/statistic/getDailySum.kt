package icu.ketal.plugins.statistic

import icu.ketal.dao.DailySum
import icu.ketal.table.DailySumDb
import icu.ketal.utils.catching
import icu.ketal.utils.now
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
        call.catching {
            val (userId, num, skip) = receive<GetDailySumReq>()
            icu.ketal.plugins.user.check(userId, request)
            val rsq = transaction {
                val dailySum =
                    DailySum.find { DailySumDb.userId.eq(userId) and DailySumDb.date.lessEq(Clock.System.now.date) }
                        .limit(num, skip)
                        .map { GetDailySumRsq.Data(it) }
                GetDailySumRsq(dailySum = dailySum)
            }
            respond(rsq)
        }
    }
}

@Serializable
data class GetDailySumReq(
    val userId: Int,
    val size: Int = 10,
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
