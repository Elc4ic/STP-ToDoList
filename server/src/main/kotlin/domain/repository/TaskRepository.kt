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
        TasksTable.deleteWhere { (id eq UUID.fromString(dto.id)) and (TasksTable.userId eq userId) }
        null
    }

    override suspend fun update(userId: UUID, dto: TaskDto): String = dbQuery {
        val taskUuid = UUID.fromString(dto.id)
        val serverTask = TasksTable.selectAll()
            .where { TasksTable.id eq taskUuid }.singleOrNull()
        if (serverTask != null) {
            val serverUpdatedAt = serverTask[TasksTable.updatedAt]

            if (dto.updatedAt > serverUpdatedAt) {
                TasksTable.update({ (TasksTable.id eq taskUuid) and (TasksTable.userId eq userId) }) {
                    it[title] = dto.title
                    it[content] = dto.content
                    it[isPinned] = dto.isPinned
                    it[updatedAt] = dto.updatedAt
                    it[deadline] = dto.deadline
                    it[progressStatus] = dto.progressStatus
                }
            }
        }
        dto.id
    }

    override suspend fun insert(userId: UUID, dto: TaskDto): String = dbQuery {
        print("insert")
        TasksTable.insertAndGetId {
            it[id] = UUID.fromString(dto.id)
            it[TasksTable.userId] = userId
            it[title] = dto.title
            it[content] = dto.content
            it[isPinned] = dto.isPinned
            it[createdAt] = dto.createdAt
            it[updatedAt] = dto.updatedAt
            it[deadline] = dto.deadline
            it[progressStatus] = dto.progressStatus
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
