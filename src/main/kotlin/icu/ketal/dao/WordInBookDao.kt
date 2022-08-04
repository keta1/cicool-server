package icu.ketal.dao

import icu.ketal.table.WordInBookDb
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class WordInBook(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<WordInBook>(WordInBookDb)

    val wordId by WordInBookDb.wordId
    val bookId by WordInBookDb.bookId
}
