package dev.stp.app.data.localDB

import androidx.datastore.preferences.protobuf.Timestamp
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import dev.stp.app.domain.entity.Task
import enums.SyncStatus
import kotlinx.coroutines.flow.Flow
import java.util.UUID


@Dao
interface TaskDao {

    @Transaction
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTask(): Flow<List<TaskDbModel>>

    @Query("SELECT * FROM tasks WHERE  syncStatus != 'SYNCHRONIZED'")
    suspend fun getAllNotSyncTask(): List<TaskDbModel>

    @Query("SELECT * FROM tasks WHERE userId = :userId")
    suspend fun getAllTasksByUserId(userId: UUID): List<TaskDbModel>

    @Query("SELECT id FROM tasks WHERE userId = :userId AND syncStatus != 'SYNCHRONIZED'")
    suspend fun getAllNotSyncTaskIdsByUserId(userId:UUID): List<UUID>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTask(task: TaskDbModel)

    @Query("""
    SELECT * FROM tasks
    WHERE deadline BETWEEN :startMillis AND :endMillis
    AND syncStatus != 'PENDING_DELETE'
    ORDER BY deadline ASC
""")
    fun getTasksForPeriod(
        startMillis: Long,
        endMillis: Long
    ): Flow<List<TaskDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTasks(tasks: List<TaskDbModel>)

    @Query("DELETE FROM tasks WHERE id == :taskId")
    suspend fun deleteTask(taskId: UUID)

    @Query("SELECT * FROM tasks WHERE id == :taskId")
    suspend fun getTask(taskId: UUID): TaskDbModel

    @Query("UPDATE tasks SET isPinned = NOT isPinned WHERE id = :taskId")
    fun switchPinned(taskId: UUID)

    @Query("UPDATE tasks SET syncStatus = :status WHERE id = :taskId")
    fun updateSyncStatus(taskId: UUID, status: SyncStatus)

    @Query(
        """
        SELECT DISTINCT *  FROM tasks
        WHERE title LIKE '%' || :query || '%' 
        OR content LIKE '%' || :query || '%' 
        ORDER BY createdAt DESC
        """
    )
    fun searchTask(query: String): Flow<List<TaskDbModel>>

    @Query("DELETE FROM tasks")
    suspend fun clearAll()

    @Transaction
    suspend fun withTransaction(block: suspend () -> Unit) = block()

}