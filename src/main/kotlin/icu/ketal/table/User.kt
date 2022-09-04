package icu.ketal.table

import icu.ketal.data.DEFAULT
import icu.ketal.utils.encodeToJson
import org.jetbrains.exposed.dao.id.IntIdTable

object UserDb : IntIdTable("user") {
    val openId = text("openid")
    val sessionKey = text("session_key")
    val salt = text("salt")
    val settings = text("settings").default("{}")
    val createTime = long("c_time").default(-1)
    val lastLogin = long("last_login").default(-1)
    val nickName = text("nick_name").default("")
    val avatarPic = text("avatar_pic").default("")
    val bookId = integer("book_id").default(-1)
    val ofMatrix = text("of_matrix").default(DEFAULT.encodeToJson())
}
