package icu.ketal.dao

import icu.ketal.table.LearnRecordDb
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class LearnRecord(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<LearnRecord>(LearnRecordDb)

    val userId by LearnRecordDb.userId
    val wordBookId by LearnRecordDb.wordBookId
    val wordId by LearnRecordDb.wordId
    val EF by LearnRecordDb.EF
    val NOI by LearnRecordDb.NOI
    val lastToLearn by LearnRecordDb.lastToLearn
    val nextToLearn by LearnRecordDb.nextToLearn
    val next_n by LearnRecordDb.next_n
    val completed by LearnRecordDb.completed
    val repeatTimes by LearnRecordDb.repeatTimes
    val master by LearnRecordDb.master
    val createTime by LearnRecordDb.createTime
}
