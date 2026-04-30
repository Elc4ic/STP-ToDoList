package dev.stp.app.data.repository

import dev.stp.app.data.localDB.TaskDao
import dev.stp.app.data.localDB.TaskDbModel
import dev.stp.app.data.mapper.toDbModel
import dev.stp.app.data.mapper.toEntity
import dev.stp.app.domain.entity.Task
import dev.stp.app.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map



class TaskRepositoryImpl(
    private val taskDao: TaskDao
) : TaskRepository {

    override suspend fun addTask(
        title: String,
        content: String,
        isPinned: Boolean,
        createdAt: Long,
        deadline: Long,
    ) {
        taskDao.addTask(
            TaskDbModel(
                id = 0,
                title = title,
                content = content,
                isPinned = isPinned,
                createdAt = createdAt,
                deadline = deadline
            )
        )
    }

    override suspend fun deleteTask(taskId: Int) {
        taskDao.deleteTask(taskId)
    }

    override suspend fun editTask(task: Task) {
        val editTask = task.toDbModel()
        taskDao.addTask(editTask)
    }

    override fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTask().map {
            it.toEntity()
        }
    }

    override suspend fun getTask(taskId: Int): Task {
        return taskDao.getTask(taskId).toEntity()
    }

    override fun searchTask(query: String): Flow<List<Task>> {
        return taskDao.searchTask(query).map {
            it.toEntity()
        }
    }

    override fun switchPinned(taskId: Int) {
        taskDao.switchPinned(taskId)
    }
}