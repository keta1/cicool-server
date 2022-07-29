package icu.ketal.dao

import icu.ketal.table.UserDb
import icu.ketal.utils.AESCrypt
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(UserDb)

    var openId by UserDb.openId
    var sessionKey by UserDb.sessionKey
    var salt by UserDb.salt
    var createTime by UserDb.createTime
    var lastLogin by UserDb.lastLogin
    var nickName by UserDb.nickName
    var avatarPic by UserDb.avatarPic
    var bookId by UserDb.bookId
    var ofMatrix by UserDb.ofMatrix

    val token: String
        get() = AESCrypt.encrypt(sessionKey, salt, openId)!!
}
