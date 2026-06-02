package dev.stp.infrastructure.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.stp.infrastructure.schema.TasksTable
import dev.stp.infrastructure.schema.UsersTable
import io.github.cdimascio.dotenv.dotenv
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
object DatabaseFactory {
    fun init(
        jdbcUrl: String = System.getenv("POSTGRES_URL") ?: "jdbc:postgresql://localhost:5432/todo_list_db",
        driverClassName: String = "org.postgresql.Driver",
        user: String = System.getenv("POSTGRES_USER") ?: dotenv { ignoreIfMissing = true }["DB_USER"] ?: "postgres",
        pass: String = System.getenv("POSTGRES_PASSWORD") ?: dotenv { ignoreIfMissing = true }["DB_PASSWORD"] ?: "postgres"
    ) {
        val database = Database.connect(createHikariDataSource(jdbcUrl, driverClassName, user, pass))

        transaction(database) {
            SchemaUtils.create(UsersTable, TasksTable)
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
