package dev.stp.infrastructure.schema

import org.jetbrains.exposed.sql.Table

object UsersTable :
    Table("users") {
    val id = uuid("id")
    val login = varchar("login", 50).uniqueIndex()
    val passwordHash = text("password_hash")
    override val primaryKey = PrimaryKey(id)
}
