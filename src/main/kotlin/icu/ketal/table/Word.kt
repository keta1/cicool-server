package icu.ketal.table

import org.jetbrains.exposed.dao.id.IntIdTable

object WordDb : IntIdTable("word") {
    val word = varchar("word", 64, "NOCASE")
    val sw = varchar("sw", 64, "NOCASE")
    val phonetic = text("phonetic").nullable()
    val definition = text("definition").nullable()
    val translation = text("translation").nullable()
    val collins = integer("collins").nullable()
    val bnc = integer("bnc").nullable().default(null)
    val frq = integer("frq").nullable().default(null)
    val exchange = text("exchange").nullable()
}
