package dev.stp.infrastructure.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.stp.infrastructure.schema.UsersTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val driverClassName = "org.postgresql.Driver"
        val jdbcUrl = System.getenv("POSTGRES_URL") ?: "jdbc:postgresql://localhost:5432/todolist_db"
        val user = System.getenv("POSTGRES_USER") ?: "postgres"
        val password = System.getenv("POSTGRES_PASSWORD") ?: "postgres"

        val database = Database.connect(createHikariDataSource(jdbcUrl, driverClassName, user, password))

        transaction(database) {
            SchemaUtils.create(UsersTable)
        }
    }

    private fun createHikariDataSource(
        url: String,
        driver: String,
        user: String,
        pass: String
    ) = HikariDataSource(HikariConfig().apply {
        driverClassName = driver
        jdbcUrl = url
        username = user
        password = pass
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    })
}