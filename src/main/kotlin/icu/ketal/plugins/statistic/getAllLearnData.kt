package icu.ketal.plugins.statistic

import icu.ketal.dao.LearnRecord
import icu.ketal.table.LearnRecordDb
import icu.ketal.utils.catching
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction

context(StatisticRouting)
fun getAllLearnData() {
    routing.post("cicool/statistic/getAllLearnData") {
        call.catching {
            val (userId) = receive<GetAllLearnDataReq>()
            icu.ketal.plugins.user.check(userId, request)
            val rsq = transaction {
                val records = LearnRecord.find { LearnRecordDb.userId eq userId }
                GetAllLearnDataRsq(
                    learn = records.count().toInt(),
                    master = records.count { it.master }
                )
            }
            respond(rsq)
        }
    }
}

@Serializable
data class GetAllLearnDataReq(
    val userId: Int
)

@Serializable
data class GetAllLearnDataRsq(
    val errcode: Int = 0,
    val errmsg: String? = null,
    val learn: Int,
    val master: Int
)
