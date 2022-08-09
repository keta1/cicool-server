package icu.ketal.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object NoteBookDb : IntIdTable("notebook") {
    val userId = integer("user_id").references(WordBookDb.id)
    val wordId = integer("word_id").references(WordDb.id)
    val creatTime = datetime("c_time")
}
