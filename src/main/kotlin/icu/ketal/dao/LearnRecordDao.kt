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
    val master by LearnRecordDb.master
    val nextToLearn by LearnRecordDb.nextToLearn
}
