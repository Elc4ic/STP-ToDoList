package dev.stp.service

import dev.stp.domain.repository.TaskRepository
import dev.stp.domain.repository.UserRepository
import dev.stp.infrastructure.schema.TasksTable
import dto.GetTaskResponse
import dto.SyncRequest
import dto.SyncResponse
import dto.SyncTaskResponse
import enums.ResultCode
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.update
import java.util.UUID

interface TaskService {
    suspend fun sync(userId: UUID, request: SyncRequest): SyncResponse
    suspend fun getAll(userId: UUID): GetTaskResponse
}

class TaskServiceImpl(
    private val taskRepository: TaskRepository
) : TaskService {

    override suspend fun sync(userId: UUID, request: SyncRequest): SyncResponse {
        return SyncResponse(request.unsyncTasks.map { dto ->
            try {
                val remoteId = when (dto.syncStatus) {
                    "PENDING_DELETE" -> taskRepository.delete(userId, dto)
                    "PENDING_UPDATE" -> taskRepository.update(userId, dto)
                    "PENDING_INSERT" -> taskRepository.insert(userId, dto)
                    else -> dto.id
                }
                SyncTaskResponse(
                    id = dto.id,
                    status = ResultCode.SUCCESS.toString()
                )
            } catch (e: Exception) {
                SyncTaskResponse(dto.id, ResultCode.ERROR.toString())
            }
        })
    }

    override suspend fun getAll(userId: UUID): GetTaskResponse {
        return GetTaskResponse(
            taskRepository.getAllByUserId(userId).map { it.toDto() }
        )
    }

}