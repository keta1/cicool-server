package icu.ketal.plugins.word

import icu.ketal.dao.Lemma
import icu.ketal.dao.Word
import icu.ketal.data.ServiceError
import icu.ketal.plugins.user.check
import icu.ketal.table.LemmaDb
import icu.ketal.table.WordDb
import icu.ketal.utils.DBUtils
import icu.ketal.utils.isTranslation
import icu.ketal.utils.logger
import icu.ketal.utils.respondError
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction

context(WordRouting)
fun getSearchResult() {
    routing.post("cicool/word/getSearchResult") {
        kotlin.runCatching {
            val (userId, keyword, getLemma, recordLimit, skip) = call.receive<GetSearchResultReq>()
            val cookie = call.request.cookies["TOKEN"]
            check(userId, cookie)?.let {
                call.respondError(it)
                return@runCatching
            }
            val isTranslation = isTranslation(keyword)
            logger.info("keyword:$keyword")
            if (!isTranslation && !keyword.contains(" ")) {
                logger.info("don't have space")
                // 如果没有空格，则查询单个
                val stems = if (getLemma) findStem(keyword).map { GetSearchResultRsq.SWord(it) } else emptyList()
                // 使用sw字段进行前缀模糊查找
                val words = findWords("${keyword.lowercase()}%", recordLimit, skip)
                    .sortedBy {
                        it.word.contains(" ")
                    }.map { GetSearchResultRsq.SWord(it) }
                call.respond(GetSearchResultRsq(lemmaSearch = stems, directSearch = words))
            } else if (!isTranslation) {
                // 有空格情况，不进行原型查找，将空格换为任意位数通配符进行匹配
                logger.info("keyword:${keyword.lowercase().replace(" ", "%")}%")
                val words = findWords(
                    "${keyword.lowercase().replace(" ", "%")}%",
                    recordLimit,
                    skip
                ).asSequence().sortedBy {
                    getIndexSum(keyword, it.word)
                }.sortedBy {
                    it.word.contains(" ")
                }.map { GetSearchResultRsq.SWord(it) }.toList()
                call.respond(GetSearchResultRsq(directSearch = words))
            } else {
                // 中文的情况
                logger.info("中文搜索")
                val words = findTrans(
                    "${keyword.lowercase().replace(" ", "%")}%",
                    recordLimit,
                    skip
                ).asSequence().sortedBy {
                    it.word.contains(" ")
                }.map { GetSearchResultRsq.SWord(it) }.toList()
                call.respond(GetSearchResultRsq(directSearch = words))
            }
            call.respond("OK")
        }.onFailure {
            logger.warn(it.stackTraceToString())
            call.respondError(ServiceError.INTERNAL_SERVER_ERROR)
        }
    }
}

private fun findStem(keyword: String): List<Word> {
    logger.info("findStem:$keyword")
    return transaction(DBUtils.wordDb) {
        val stem = Lemma.find { LemmaDb.word eq keyword }
        logger.info("stem count: " + stem.count())
        val lemmaSearch = stem.mapNotNull {
            Word.find { WordDb.word eq it.stem }.firstOrNull()
        }
        logger.info("lemmaSearch: $lemmaSearch")
        lemmaSearch
    }
}

private fun findWords(
    keyword: String,
    recordLimit: Int = 20,
    skip: Long = 0
): List<Word> {
    return transaction(DBUtils.wordDb) {
        Word.find {
            WordDb.sw like keyword
        }.limit(recordLimit, skip).toList()
    }
}

private fun findTrans(
    keyword: String,
    recordLimit: Int = 20,
    skip: Long = 0
): List<Word> {
    return transaction(DBUtils.wordDb) {
        Word.find {
            WordDb.translation like "%${keyword.lowercase()}%"
        }.limit(recordLimit, skip).toList()
    }
}

private fun getIndexSum(keyword: String, word: String): Int {
    val kwSpiltBySpace = keyword.split(" ")
    var accLen = 0
    return kwSpiltBySpace.sumOf {
        val index = word.indexOf(it, accLen)
        accLen += it.length
        if (index == -1) 0 else index
    }
}

@Serializable
data class GetSearchResultReq(
    val userId: Int,
    val keyword: String,
    val getLemma: Boolean = true,
    val recordLimit: Int = 20,
    val skip: Long = 0
)

@Serializable
data class GetSearchResultRsq(
    val errcode: Int = 0,
    val errmsg: String? = null,
    val lemmaSearch: List<SWord> = emptyList(),
    val directSearch: List<SWord> = emptyList()
) {
    @Serializable
    data class SWord(
        val id: Int,
        val word: String,
        val translation: String?
    ) {
        constructor(words: Word) : this(
            words.id.value,
            words.word,
            words.translation
        )
    }
}
