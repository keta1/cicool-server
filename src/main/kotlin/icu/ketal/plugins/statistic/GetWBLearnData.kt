package icu.ketal.plugins.statistic

import icu.ketal.dao.LearnRecord
import icu.ketal.dao.WordInBook
import icu.ketal.table.LearnRecordDb
import icu.ketal.table.WordInBookDb
import icu.ketal.utils.catching
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction

context(StatisticRouting)
fun getWBLearnData() {
    routing.post("cicool/statistic/getWBLearnData") {
        call.catching {
            val (userId, bookId) = receive<GetWBLearnDataReq>()
            icu.ketal.plugins.user.check(userId, request)
            val rsq = transaction {
                val record = LearnRecord.find { LearnRecordDb.userId eq userId }.toList()
                val recordId = record.map { it.wordId }
                val words = WordInBook.find { WordInBookDb.bookId.eq(bookId) }.toList()
                val learned = words.filter { it.wordId in recordId }
                val total = words.size
                GetWBLearnDataRsq(
                    data = GetWBLearnDataRsq.Data(
                        total - learned.size,
                        learned.size,
                        learned.filter { word -> record.first { it.wordId == word.wordId }.master }.size
                    )
                )
            }
            respond(rsq)
        }
    }
}

@Serializable
data class GetWBLearnDataReq(
    var userId: Int,
    var bookId: Int
)

@Serializable
data class GetWBLearnDataRsq(
    val errcode: Int = 0,
    val errmsg: String? = null,
    val data: Data
) {
    @Serializable
    data class Data(
        val notLearn: Int,
        val learn: Int,
        val master: Int
    )
}
