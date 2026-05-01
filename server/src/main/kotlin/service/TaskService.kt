package dev.stp.service

import dev.stp.infrastructure.schema.TasksTable
import dto.SyncRequest
import dto.SyncResponse
import dto.SyncTaskResponse
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.update
import java.util.UUID

interface TaskService {
    suspend fun sync(userId: UUID, request: SyncRequest): SyncResponse
}

class TaskServiceImpl : TaskService {

    override suspend fun sync(userId: UUID, request: SyncRequest): SyncResponse {
        return SyncResponse(request.unsyncTasks.map { dto ->
            try {
                val remoteId = when (dto.syncStatus) {
                    "PENDING_DELETE" -> {
                        TasksTable.deleteWhere { (id eq UUID.fromString(dto.remoteId)) and (TasksTable.userId eq userId) }
                        null
                    }

                    "PENDING_UPDATE" -> {
                        TasksTable.update({ (TasksTable.id eq UUID.fromString(dto.remoteId)) and (TasksTable.userId eq userId) }) {
                            it[title] = dto.title
                            it[content] = dto.content
                            it[isPinned] = dto.isPinned
                            it[deadline] = dto.deadline
                        }
                        dto.remoteId
                    }

                    "PENDING_INSERT" -> TasksTable.insertAndGetId {
                        it[TasksTable.userId] = userId
                        it[title] = dto.title
                        it[content] = dto.content
                        it[isPinned] = dto.isPinned
                        it[createdAt] = dto.createdAt
                        it[deadline] = dto.deadline
                    }.value.toString()

                    else -> dto.remoteId
                }
                SyncTaskResponse(
                    localId = dto.id,
                    remoteId = remoteId,
                    status = "SUCCESS" //TODO вынести как состояния в shared
                )
            } catch (e: Exception) {
                SyncTaskResponse(dto.id, null, "ERROR")
            }
        })
    }

}