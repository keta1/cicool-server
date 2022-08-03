package icu.ketal.dao

import icu.ketal.table.LemmaDb
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Lemma(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Lemma>(LemmaDb)

    var stem by LemmaDb.stem
    var word by LemmaDb.word
}
