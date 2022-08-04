package icu.ketal.dao

import icu.ketal.table.WordBookDb
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class WordBook(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<WordBook>(WordBookDb)

    val name by WordBookDb.name
    val description by WordBookDb.description
    val total by WordBookDb.total
    val coverType by WordBookDb.coverType
    val color by WordBookDb.color
    val coverUrl by WordBookDb.coverUrl
}
