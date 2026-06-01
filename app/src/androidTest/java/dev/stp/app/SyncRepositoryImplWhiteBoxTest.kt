package dev.stp.app

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.stp.app.data.datasource.DataStoreTokenStore
import dev.stp.app.data.localDB.TaskDao
import dev.stp.app.data.localDB.TaskDataBase
import dev.stp.app.data.localDB.TaskDbModel
import dev.stp.app.data.repository.SyncRepositoryImpl
import enums.SyncStatus
import io.ktor.client.HttpClient
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class SyncRepositoryImplWhiteBoxTest {

    private lateinit var db: TaskDataBase
    private lateinit var taskDao: TaskDao
    private lateinit var client: HttpClient
    private lateinit var repository: SyncRepositoryImpl

    private val userId: UUID = UUID.fromString("00000000-0000-0000-0000-000000000001")

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        db = Room.inMemoryDatabaseBuilder(context, TaskDataBase::class.java)
            .allowMainThreadQueries()
            .build()

        taskDao = db.taskDao()
        client = HttpClient()

        repository = SyncRepositoryImpl(
            context = context,
            taskDao = taskDao,
            client = client,
            tokenStore = DataStoreTokenStore(context)
        )
    }

    @After
    fun tearDown() {
        client.close()
        db.close()
    }

    @Test
    fun path1_noServerTasksAndNoLocalTasks_databaseRemainsEmpty() = runBlocking {
        // CFG path: 1 -> 2 -> 3 -> 4F -> 7 -> 8 -> 9F -> 13
        repository.syncLocalDatabaseWithServer(
            serverTasks = emptyList(),
            userId = userId
        )

        val actualTasks = taskDao.getAllTasksByUserId(userId)

        assertTrue(actualTasks.isEmpty())
    }

    @Test
    fun path2_serverTaskIsNotPending_addsServerTaskAsSynchronized() = runBlocking {
        // CFG path: 1 -> 2 -> 3 -> 4T -> 5T -> 6 -> 4F -> 7 -> 8 -> 9T -> 10F -> 9F -> 13
        val serverTask = task(id = uuid(2), syncStatus = SyncStatus.PENDING_INSERT)

        repository.syncLocalDatabaseWithServer(
            serverTasks = listOf(serverTask),
            userId = userId
        )

        val actualTasks = taskDao.getAllTasksByUserId(userId)

        assertEquals(1, actualTasks.size)
        assertEquals(serverTask.id, actualTasks.single().id)
        assertEquals(SyncStatus.SYNCHRONIZED, actualTasks.single().syncStatus)
    }

    @Test
    fun path3_serverTaskAlreadyHasPendingLocalVersion_doesNotOverwritePendingLocalTask() = runBlocking {
        // CFG path: 1 -> 2 -> 3 -> 4T -> 5F -> 4F -> 7 -> 8 -> 9T -> 10F -> 9F -> 13
        val taskId = uuid(3)
        val pendingLocalTask = task(
            id = taskId,
            title = "local pending title",
            syncStatus = SyncStatus.PENDING_INSERT
        )
        val serverTaskWithSameId = task(
            id = taskId,
            title = "server title",
            syncStatus = SyncStatus.SYNCHRONIZED
        )
        taskDao.addTask(pendingLocalTask)

        repository.syncLocalDatabaseWithServer(
            serverTasks = listOf(serverTaskWithSameId),
            userId = userId
        )

        val actualTask = taskDao.getAllTasksByUserId(userId).single()

        assertEquals(taskId, actualTask.id)
        assertEquals("local pending title", actualTask.title)
        assertEquals(SyncStatus.PENDING_INSERT, actualTask.syncStatus)
    }

    @Test
    fun path4_localSynchronizedTaskIsAbsentOnServer_deletesLocalTask() = runBlocking {
        // CFG path: 1 -> 2 -> 3 -> 4F -> 7 -> 8 -> 9T -> 10T -> 11T -> 12 -> 9F -> 13
        val localOnlyTask = task(id = uuid(4), syncStatus = SyncStatus.SYNCHRONIZED)
        taskDao.addTask(localOnlyTask)

        repository.syncLocalDatabaseWithServer(
            serverTasks = emptyList(),
            userId = userId
        )

        val actualTasks = taskDao.getAllTasksByUserId(userId)

        assertFalse(actualTasks.any { it.id == localOnlyTask.id })
    }

    @Test
    fun path5_localPendingDeleteTaskIsAbsentOnServer_doesNotDeleteLocalTask() = runBlocking {
        // CFG path: 1 -> 2 -> 3 -> 4F -> 7 -> 8 -> 9T -> 10T -> 11F -> 9F -> 13
        val localPendingTask = task(id = uuid(5), syncStatus = SyncStatus.PENDING_DELETE)
        taskDao.addTask(localPendingTask)

        repository.syncLocalDatabaseWithServer(
            serverTasks = emptyList(),
            userId = userId
        )

        val actualTask = taskDao.getAllTasksByUserId(userId).single()

        assertEquals(localPendingTask.id, actualTask.id)
        assertEquals(SyncStatus.PENDING_DELETE, actualTask.syncStatus)
    }

    @Test
    fun path6_localTaskExistsOnServer_firstDeleteConditionIsFalse_taskIsNotDeleted() = runBlocking {
        // CFG path: 1 -> 2 -> 3 -> 4T -> 5F -> 4F -> 7 -> 8 -> 9T -> 10F -> 9F -> 13
        // This path explicitly covers the false branch of:
        // !serverTaskIds.contains(localTask.id)
        val taskId = uuid(6)
        val localPendingTask = task(
            id = taskId,
            title = "local task must stay",
            syncStatus = SyncStatus.PENDING_INSERT
        )
        val serverTaskWithSameId = task(
            id = taskId,
            title = "server task with same id",
            syncStatus = SyncStatus.SYNCHRONIZED
        )
        taskDao.addTask(localPendingTask)

        repository.syncLocalDatabaseWithServer(
            serverTasks = listOf(serverTaskWithSameId),
            userId = userId
        )

        val actualTasks = taskDao.getAllTasksByUserId(userId)
        val actualTask = actualTasks.single()

        assertEquals(taskId, actualTask.id)
        assertEquals("local task must stay", actualTask.title)
        assertEquals(SyncStatus.PENDING_INSERT, actualTask.syncStatus)
    }

    private fun task(
        id: UUID,
        title: String = "task-$id",
        syncStatus: SyncStatus
    ): TaskDbModel = TaskDbModel(
        id = id,
        userId = userId,
        title = title,
        content = "content-$id",
        isPinned = false,
        createdAt = 1L,
        updatedAt = 1L,
        deadline = 1L,
        syncStatus = syncStatus
    )

    private fun uuid(value: Int): UUID = UUID.fromString(
        "00000000-0000-0000-0000-${value.toString().padStart(12, '0')}"
    )
}
