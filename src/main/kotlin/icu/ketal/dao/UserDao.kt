package icu.ketal.dao

import icu.ketal.table.UserDb
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(UserDb)

    var openId by UserDb.openId
    var createTime by UserDb.createTime
    var lastLogin by UserDb.lastLogin
    var nickName by UserDb.nickName
    var avatarPic by UserDb.avatarPic
    var bookId by UserDb.bookId
    var settings by UserDb.settings
    var ofMatrix by UserDb.ofMatrix
}
