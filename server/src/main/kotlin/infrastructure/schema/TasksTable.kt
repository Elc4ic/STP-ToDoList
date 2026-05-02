package dev.stp.infrastructure.schema

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption

object TasksTable : IntIdTable("tasks") {
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE)
    val title = varchar("title", 255)
    val content = text("content")
    val isPinned = bool("is_pinned").default(false)
    val createdAt = long("created_at")
    val deadline = long("deadline")
}