package icu.ketal.dao

import icu.ketal.table.WordDb
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

val wordTable = WordDb.word
val wordAllTable = WordDb.wordAll

interface WordDao {
    val id: EntityID<Int>
    val sw: String
    val word: String
    val phonetic: String?
    val definition: String?
    val translation: String?
    val pos: String?
    val collins: Int
    val oxford: Int
    val tag: String?
    val bnc: Int?
    val frq: Int?
    val exchange: String?
}

class Word(id: EntityID<Int>) : IntEntity(id), WordDao {
    companion object : IntEntityClass<Word>(wordTable)

    override val sw by wordTable.sw
    override val word by wordTable.word
    override val phonetic by wordTable.phonetic
    override val definition by wordTable.definition
    override val translation by wordTable.translation
    override val pos by wordTable.pos
    override val collins by wordTable.collins
    override val oxford by wordTable.oxford
    override val tag by wordTable.tag
    override val bnc by wordTable.bnc
    override val frq by wordTable.frq
    override val exchange by wordTable.exchange
}

// so stupid code!
class WordAll(id: EntityID<Int>) : IntEntity(id), WordDao {
    companion object : IntEntityClass<Word>(wordAllTable)

    override val sw by wordAllTable.sw
    override val word by wordAllTable.word
    override val phonetic by wordAllTable.phonetic
    override val definition by wordAllTable.definition
    override val translation by wordAllTable.translation
    override val pos by wordAllTable.pos
    override val collins by wordAllTable.collins
    override val oxford by wordAllTable.oxford
    override val tag by wordAllTable.tag
    override val bnc by wordAllTable.bnc
    override val frq by wordAllTable.frq
    override val exchange by wordAllTable.exchange
}
