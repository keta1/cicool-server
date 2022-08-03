package icu.ketal.table

import org.jetbrains.exposed.dao.id.IntIdTable

class WordDb(name: String) : IntIdTable(name) {
    val word = varchar("word", 64, "NOCASE")
    val sw = varchar("sw", 64, "NOCASE")
    val phonetic = text("phonetic").nullable()
    val definition = text("definition").nullable()
    val translation = text("translation").nullable()
    val pos = text("pos").nullable()
    val collins = integer("collins").default(0)
    val oxford = integer("oxford").default(0)
    val tag = text("tag").nullable()
    val bnc = integer("bnc").nullable().default(null)
    val frq = integer("frq").nullable().default(null)
    val exchange = text("exchange").nullable()

    companion object {
        val word = WordDb("word")
        val wordAll = WordDb("word_all")
    }
}
