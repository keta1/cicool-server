package icu.ketal.plugins.statistic

import icu.ketal.dao.LearnRecord
import icu.ketal.data.ServiceError
import icu.ketal.table.LearnRecordDb
import icu.ketal.utils.logger
import icu.ketal.utils.respondError
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction

context(StatisticRouting)
fun getAllLearnData() {
    routing.post("cicool/statistic/getAllLearnData") {
        kotlin.runCatching {
            val (userId) = call.receive<GetAllLearnDataReq>()
            val rsq = transaction {
                val records = LearnRecord.find { LearnRecordDb.userId eq userId }
                GetAllLearnDataRsq(
                    errcode = 200,
                    data = GetAllLearnDataRsq.Data(
                        records.count().toInt(),
                        records.count { it.master }
                    )
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
data class GetAllLearnDataReq(
    val userId: Int,
)

@Serializable
data class GetAllLearnDataRsq(
    val errcode: Int = 0,
    val errmsg: String? = null,
    val data: Data
) {
    @Serializable
    data class Data(
        val learn: Int,
        val master: Int
    )
}
