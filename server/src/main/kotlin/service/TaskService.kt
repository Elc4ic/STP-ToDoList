package dev.stp.service

import apiRoutes.Api
import arrow.core.Either
import arrow.core.raise.either
import dev.stp.domain.repository.TaskRepository
import dev.stp.domain.repository.UserRepository
import dev.stp.infrastructure.schema.TasksTable
import dto.GetTaskResponse
import dto.SyncRequest
import dto.SyncResponse
import dto.SyncTaskResponse
import enums.ResultCode
import enums.SyncStatus
import errors.AppError
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.update
import java.util.UUID

interface TaskService {
    suspend fun sync(userId: UUID, request: SyncRequest): SyncResponse
    suspend fun getAll(userId: UUID): Either<AppError, GetTaskResponse>
}

class TaskServiceImpl(
    private val taskRepository: TaskRepository
) : TaskService {

    override suspend fun sync(userId: UUID, request: SyncRequest): SyncResponse {
        return SyncResponse(request.unsyncTasks.map { dto ->
            Either.catch {
                when (SyncStatus.valueOf(dto.syncStatus)) {
                    SyncStatus.PENDING_DELETE -> taskRepository.delete(userId, dto)
                    SyncStatus.PENDING_UPDATE -> taskRepository.update(userId, dto)
                    SyncStatus.PENDING_INSERT -> taskRepository.insert(userId, dto)
                    else -> dto.id
                }
                SyncTaskResponse(
                    id = dto.id,
                    status = ResultCode.SUCCESS.toString()
                )
            }.fold(
                ifLeft = { error ->
                    println("Ошибка синхронизации: ${error.message}")
                    SyncTaskResponse(dto.id, ResultCode.ERROR.toString())
                },
                ifRight = {
                    SyncTaskResponse(dto.id, ResultCode.SUCCESS.toString())
                }
            )
        })
    }

    override suspend fun getAll(userId: UUID): Either<AppError, GetTaskResponse> = either {
        val tasks = Either.catch { taskRepository.getAllByUserId(userId) }
            .mapLeft { AppError.Task.Server.DatabaseError() }
            .bind()

        GetTaskResponse(tasks.map { it.toDto() })
    }

}