package icu.ketal.dao

import icu.ketal.table.NoteBookDb
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class NoteBook(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<NoteBook>(NoteBookDb)

    var userId by NoteBookDb.userId
    var wordId by NoteBookDb.wordId
    var creatTime by NoteBookDb.creatTime
}
