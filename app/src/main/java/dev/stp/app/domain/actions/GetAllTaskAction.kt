package dev.stp.app.domain.actions

import arrow.core.Either
import dev.stp.app.domain.entity.Task
import dev.stp.app.domain.repository.TaskRepository
import enums.ProgressStatus
import enums.SyncStatus
import errors.AppError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

context(repo: TaskRepository)
fun getVisibleTask(): Flow<Either<AppError, List<Task>>> =
    repo.getAllTasks().fold(
        ifLeft = { error ->
            flowOf(Either.Left(error))
        },
        ifRight = { flow ->
            flow.map { tasks ->
                val filtered = tasks.filter {
                    it.syncStatus != SyncStatus.PENDING_DELETE || it.progressStatus != ProgressStatus.CANCELED
                }
                Either.Right(filtered)
            }
        }
    )