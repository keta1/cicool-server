package icu.ketal.dao

import icu.ketal.table.WordDb
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Word(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Word>(WordDb)

    val sw by WordDb.sw
    val word by WordDb.word
    val phonetic by WordDb.phonetic
    val definition by WordDb.definition
    val translation by WordDb.translation
    val collins by WordDb.collins
    val bnc by WordDb.bnc
    val frq by WordDb.frq
    val exchange by WordDb.exchange
}
