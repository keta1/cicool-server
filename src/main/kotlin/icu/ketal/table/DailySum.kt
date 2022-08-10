package icu.ketal.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.duration

object DailySumDb : IntIdTable("daily_sum") {
    val userId = integer("user_id").references(UserDb.id)
    val date = date("date")
    val learn = integer("learn")
    val review = integer("review")
    val learnTime = duration("l_time")
}
