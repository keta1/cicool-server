package icu.ketal.utils

import icu.ketal.table.UserDb
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

object DBUtils {

    val wordDb: Database = Database.connect(
        "jdbc:sqlite:word.db",
        "org.sqlite.JDBC"
    )

    val db: Database = Database.connect(
        "jdbc:sqlite:cicool.db",
        "org.sqlite.JDBC"
    )


    fun prepare() = Unit

    init {
        transaction(wordDb) {
            addLogger(StdOutSqlLogger)
        }
        transaction(db) {
            addLogger(StdOutSqlLogger)

            SchemaUtils.createMissingTablesAndColumns(UserDb)
        }
    }
}
