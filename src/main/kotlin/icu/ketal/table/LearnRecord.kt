package icu.ketal.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object LearnRecordDb : IntIdTable("learning_record") {
    val userId = integer("user_id")
    val wordBookId = integer("wd_bk_id")
    val wordId = integer("word_id")
    val master = bool("master")
    val nextToLearn = datetime("next_l")
}
