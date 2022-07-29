package icu.ketal.table

import icu.ketal.utils.encodeToJson
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

object UserDb : IntIdTable() {
    val openId = text("openid")
    val sessionKey = text("session_key")
    val salt = text("salt")
    val createTime = long("c_time").default(-1)
    val lastLogin = long("last_login").default(-1)
    val nickName = text("nick_name").default("")
    val avatarPic = text("avatar_pic").default("")
    val bookId = integer("book_id").default(-1)
    val ofMatrix = text("of_matrix").default(
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
