package icu.ketal.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object LearnRecordDb : IntIdTable("learning_record") {
    val userId = integer("user_id").references(UserDb.id)
    val wordId = integer("word_id").references(WordDb.id)
    val EF = varchar("EF", length = 10).default("2.5")
    val NOI = integer("NOI").default(1)
    val lastToLearn = timestamp("last_l")
    val nextToLearn = timestamp("next_l")
    val next_n = integer("next_n").default(0)
    val completed = bool("completed")
    val repeatTimes = integer("repeatTimes").default(0)
    val master = bool("master").default(false)
    val createTime = timestamp("c_time")
}
