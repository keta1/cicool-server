package icu.ketal.table

import org.jetbrains.exposed.dao.id.IntIdTable

object LemmaDb : IntIdTable("lemma") {
    val stem = varchar("stem", 64)
    val word = varchar("word", 64).index()
}
