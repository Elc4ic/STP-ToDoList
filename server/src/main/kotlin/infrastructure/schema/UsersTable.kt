package dev.stp.infrastructure.schema

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Table

object UsersTable : UUIDTable("users") {
    val login = varchar("login", 50).uniqueIndex()
    val passwordHash = text("password_hash")
}
