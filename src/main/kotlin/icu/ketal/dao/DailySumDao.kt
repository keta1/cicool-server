package icu.ketal.dao

import icu.ketal.table.DailySumDb
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class DailySum(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DailySum>(DailySumDb)

    var userId by DailySumDb.userId
    var date by DailySumDb.date
    var learn by DailySumDb.learn
    var review by DailySumDb.review
    var learnTime by DailySumDb.learnTime
}
