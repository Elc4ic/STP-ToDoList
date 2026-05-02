package dev.stp.domain.repository

import dev.stp.domain.entity.TaskEntity
import dev.stp.domain.entity.toTaskEntity
import dev.stp.infrastructure.schema.TasksTable
import dto.TaskDto
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import java.util.UUID

interface TaskRepository {
    suspend fun delete(userId: UUID, dto: TaskDto): String?
    suspend fun update(userId: UUID, dto: TaskDto): String?
    suspend fun insert(userId: UUID, dto: TaskDto): String
    suspend fun getAllByUserId(userId: UUID): List<TaskEntity>
}

class PostgresTaskRepository : TaskRepository {
    override suspend fun delete(userId: UUID, dto: TaskDto): String? = dbQuery {
        TasksTable.deleteWhere { (id eq dto.id) and (TasksTable.userId eq userId) }
        null
    }

    override suspend fun update(userId: UUID, dto: TaskDto): String? = dbQuery {
        TasksTable.update({ (TasksTable.id eq dto.id) and (TasksTable.userId eq userId) }) {
            it[title] = dto.title
            it[content] = dto.content
            it[isPinned] = dto.isPinned
            it[deadline] = dto.deadline
        }
        dto.remoteId
    }

    override suspend fun insert(userId: UUID, dto: TaskDto): String = dbQuery {
        TasksTable.insertAndGetId {
            it[TasksTable.userId] = userId
            it[title] = dto.title
            it[content] = dto.content
            it[isPinned] = dto.isPinned
            it[createdAt] = dto.createdAt
            it[deadline] = dto.deadline
        }.value.toString()
    }

    override suspend fun getAllByUserId(userId: UUID): List<TaskEntity> = dbQuery {
        TasksTable.selectAll()
            .where { TasksTable.userId eq userId }
            .map { it.toTaskEntity() }
    }

}

suspend fun <T> dbQuery(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }
