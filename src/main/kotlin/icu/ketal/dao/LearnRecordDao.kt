package icu.ketal.dao

import icu.ketal.table.LearnRecordDb
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class LearnRecord(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<LearnRecord>(LearnRecordDb)

    var userId by LearnRecordDb.userId
    var wordId by LearnRecordDb.wordId
    var lastToLearn by LearnRecordDb.lastToLearn
    var nextToLearn by LearnRecordDb.nextToLearn
    var completed by LearnRecordDb.completed
    var repeatTimes by LearnRecordDb.repeatTimes
    var learnTimes by LearnRecordDb.learnTimes
    var master by LearnRecordDb.master
    var createTime by LearnRecordDb.createTime
}
