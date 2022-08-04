package icu.ketal.table

import org.jetbrains.exposed.dao.id.IntIdTable

object WordInBookDb : IntIdTable("word_in_book") {
    val wordId = integer("word_id").references(WordDb.id)
    val bookId = integer("book_id").references(WordBookDb.id)
}
