package icu.ketal.table

import icu.ketal.utils.encodeToJson
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

object UserDb : IntIdTable() {
    val openId = varchar("openid", length = 128)
    val createTime = long("c_time").default(-1)
    val lastLogin = long("last_login").default(-1)
    val nickName = varchar("nick_name", length = 128).default("")
    val avatarPic = varchar("avatar_pic", length = 512).default("")
    val bookId = integer("book_id").default(-1)
    val settings = varchar("settings", length = 1024).default("")
    val ofMatrix = varchar("of_matrix", length = 1024).default(
        OFMatrix(
            HashMap<String, Collection<Int>>().apply {
                (13..28).map { it * 0.1 }
                    .forEach {
                        put("%.1f".format(it), List(5) { 0 })
                    }
            }
        ).encodeToJson()
    )
}

@Serializable
data class OFMatrix(var matrix: Map<String, Collection<Int>>)
