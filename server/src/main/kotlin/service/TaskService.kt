package dev.stp.service

import arrow.core.Either
import arrow.core.raise.either
import dev.stp.domain.repository.TaskRepository
import dto.GetTaskResponse
import dto.SyncRequest
import dto.SyncResponse
import dto.SyncTaskResponse
import enums.ResultCode
import enums.SyncStatus
import errors.IError
import java.util.UUID

interface TaskService {
    suspend fun sync(userId: UUID, request: SyncRequest): SyncResponse
    suspend fun getAll(userId: UUID): Either<IError, GetTaskResponse>
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

    override suspend fun getAll(userId: UUID): Either<IError, GetTaskResponse> = either {
        val tasks = Either.catch { taskRepository.getAllByUserId(userId) }
            .mapLeft { IError.DB.NotFoundRemote() }
            .bind()

        GetTaskResponse(tasks.map { it.toDto() })
    }

}