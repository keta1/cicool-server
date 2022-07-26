package icu.ketal.utils

import icu.ketal.table.UserDb
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

object DBUtils {

    private val db: Database = Database.connect(
        "jdbc:sqlite:cicool.db",
        "org.sqlite.JDBC"
    )

    fun prepare() = Unit

    init {
        transaction(db) {
            addLogger(StdOutSqlLogger)

            SchemaUtils.createMissingTablesAndColumns(UserDb)
        }
    }
}
