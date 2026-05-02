package dev.stp.app.data.localDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import java.util.UUID


@Dao
interface TaskDao {

    @Transaction
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
     fun getAllTask() : Flow<List<TaskDbModel>>

    @Query("SELECT * FROM tasks WHERE syncStatus != 'SYNCHRONIZED'")
    suspend fun getAllNotSyncTask() : List<TaskDbModel>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTask(task: TaskDbModel)

    @Query("DELETE FROM tasks WHERE id == :taskId")
    suspend fun deleteTask(taskId:UUID)

    @Query("SELECT * FROM tasks WHERE id == :taskId")
    suspend fun getTask(taskId: UUID) : TaskDbModel

    @Query("UPDATE tasks SET isPinned = NOT isPinned WHERE id = :taskId")
    fun switchPinned(taskId:UUID)

    @Query("""
        SELECT DISTINCT *  FROM tasks
        WHERE title LIKE '%' || :query || '%' 
        OR content LIKE '%' || :query || '%' 
        ORDER BY createdAt DESC
        """)
    fun searchTask(query: String): Flow<List<TaskDbModel>>

}