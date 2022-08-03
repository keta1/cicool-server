package icu.ketal.plugins.word

import icu.ketal.dao.Lemma
import icu.ketal.dao.Word
import icu.ketal.dao.WordAll
import icu.ketal.dao.WordDao
import icu.ketal.dao.wordAllTable
import icu.ketal.dao.wordTable
import icu.ketal.data.ServiceError
import icu.ketal.plugins.user.check
import icu.ketal.table.LemmaDb
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
            val req = call.receive<GetSearchResultReq>()
            val cookie = call.request.cookies["TOKEN"]
            check(req.id, cookie)?.let {
                call.respondError(it)
                return@runCatching
            }
            val recordLimit = if (req.useBigDb) 30 else 20
            val isTranslation = isTranslation(req.keyword)
            val keyword = req.keyword
            logger.info("keyword:$keyword")
            if (!isTranslation && !keyword.contains(" ")) {
                logger.info("don't have space")
                // 如果没有空格，则查询单个
                val stems = findStem(keyword).map { GetSearchResultRsq.SWord(it) }
                // 使用sw字段进行前缀模糊查找
                val words = findWords(
                    "${req.keyword.lowercase()}%",
                    req.useBigDb,
                    recordLimit,
                    req.skip
                ).map { GetSearchResultRsq.SWord(it) }
                call.respond(GetSearchResultRsq(lemmaSearch = stems, directSearch = words))
            } else if (!isTranslation) {
                // 有空格情况，不进行原型查找，将空格换为任意位数通配符进行匹配
                logger.info("keyword:${keyword.lowercase().replace(" ", "%")}%")
                val words = findWords(
                    "${keyword.lowercase().replace(" ", "%")}%",
                    req.useBigDb,
                    recordLimit,
                    req.skip
                ).asSequence().sortedBy {
                    getIndexSum(keyword, it.word)
                }.map { GetSearchResultRsq.SWord(it) }.toList()
                call.respond(GetSearchResultRsq(directSearch = words))
            } else {
                // 中文的情况
                logger.info("中文搜索")
                val words = findTrans(
                    "${keyword.lowercase().replace(" ", "%")}%",
                    req.useBigDb,
                    recordLimit,
                    req.skip
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

private fun findStem(keyword: String): List<WordDao> {
    logger.info("findStem:$keyword")
    return transaction(DBUtils.wordDb) {
        val stem = Lemma.find { LemmaDb.word eq keyword }
        logger.info("stem count: " + stem.count())
        val lemmaSearch = stem.mapNotNull {
            Word.find { wordTable.word eq it.stem }.firstOrNull()
        }
        logger.info("lemmaSearch: $lemmaSearch")
        lemmaSearch
    }
}

private fun findWords(
    keyword: String,
    useBigDb: Boolean = false,
    recordLimit: Int = 20,
    skip: Long = 0
): List<WordDao> {
    val table = if (useBigDb) WordAll else Word
    val column = if (useBigDb) wordAllTable.sw else wordTable.sw
    return transaction(DBUtils.wordDb) {
        table.find {
            column like "${keyword.lowercase()}%"
        }.limit(recordLimit, skip).toList()
    }
}

private fun findTrans(
    keyword: String,
    useBigDb: Boolean = false,
    recordLimit: Int = 20,
    skip: Long = 0
): List<WordDao> {
    val table = if (useBigDb) WordAll else Word
    val column = if (useBigDb) wordAllTable.translation else wordTable.translation
    return transaction(DBUtils.wordDb) {
        table.find {
            column like "%${keyword.lowercase()}%"
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
    val id: Int,
    val keyword: String,
    val useBigDb: Boolean = false,
    val getLemma: Boolean = true,
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
        constructor(words: WordDao) : this(
            words.id.value,
            words.word,
            words.translation
        )
    }
}
