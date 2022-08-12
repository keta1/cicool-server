package icu.ketal.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object NoteBookDb : IntIdTable("notebook") {
    val userId = integer("user_id").references(WordBookDb.id)
    val wordId = integer("word_id").references(WordDb.id)
    val creatTime = timestamp("c_time")
}
