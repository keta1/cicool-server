package icu.ketal.plugins.word

import icu.ketal.dao.NoteBook
import icu.ketal.data.ServiceError
import icu.ketal.table.NoteBookDb
import icu.ketal.utils.catching
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

context(WordRouting)
fun toggleAddToNB() {
    routing.post("cicool/word/toggleAddToNB") {
        call.catching {
            val (userId, wordId, add) = receive<ToggleAddToNBReq>()
            transaction {
                if (add) {
                    NoteBook.new {
                        this.userId = userId
                        this.wordId = wordId
                        this.creatTime = Clock.System.now()
                    }
                } else {
                    // drop note
                    NoteBook.find {
                        NoteBookDb.userId.eq(userId) and (NoteBookDb.wordId eq wordId)
                    }.firstOrNull()?.delete()
                }
            }
            respond(ServiceError.OK)
        }
    }
}

@Serializable
data class ToggleAddToNBReq(
    val userId: Int,
    val wordId: Int,
    val add: Boolean
)
