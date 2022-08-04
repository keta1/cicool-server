package icu.ketal.table

import org.jetbrains.exposed.dao.id.IntIdTable

object WordBookDb : IntIdTable("word_book") {
    val name = text("name")
    val description = text("description")
    val total = integer("total")
    val coverType = text("coverType")
    val color = text("color").nullable()
    val coverUrl = text("cover_url").nullable()
}
