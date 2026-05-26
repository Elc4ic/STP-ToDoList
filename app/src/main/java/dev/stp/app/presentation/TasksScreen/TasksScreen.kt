@file:OptIn(ExperimentalMaterial3Api::class)

package dev.stp.app.presentation.TasksScreen


import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.stp.app.R
import dev.stp.app.domain.entity.Task
import dev.stp.app.presentation.components.AccountBottomSheet
import dev.stp.app.presentation.components.ChangeProgressStatusDialog
import dev.stp.app.presentation.components.SearchBar
import dev.stp.app.presentation.components.SwitchScreen
import dev.stp.app.presentation.components.TaskCard
import org.koin.androidx.compose.koinViewModel


@Composable
fun TasksScreen(
    modifier: Modifier = Modifier,
    vm: TaskViewModel = koinViewModel(),
    onTaskClick: (Task) -> Unit,
    addTaskClick: () -> Unit,
    notifyClick: () -> Unit,
    settingsClick: () -> Unit,
    onSchedule: () -> Unit
) {
    val state by vm.state.collectAsState()
    var selectedTask by remember { mutableStateOf<Task?>(null) }
    var showAuthSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        floatingActionButton = {
            FloatingActionButton(
                onClick = addTaskClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add task",
                )
            }
        },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(),
                title = {
                    Icon(
                        modifier = Modifier.padding(start = 10.dp),
                        painter = painterResource(R.drawable.ic_on_time),
                        contentDescription = "on time",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                },
                actions = {
                    IconButton(onClick = { notifyClick() }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(24.dp))
                    IconButton(onClick = { settingsClick() }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(24.dp))
                    IconButton(onClick = { showAuthSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(24.dp))
                }
            )
        },
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                SwitchScreen(onSchedule = onSchedule)
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                SearchBar(
                    query = state.query
                ) { vm.processCommand(TasksCommands.InputQuery(it)) }
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
            state.pinnedTasks.forEach { task ->
                item(key = task.id) {
                    TaskCard(
                        task = task,
                        onLongClick = { selectedTask = task },
                        onTaskClick = onTaskClick
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            state.tasks.forEach {
                item(key = it.id) {
                    TaskCard(
                        task = it,
                        onLongClick = {},
                        onTaskClick = onTaskClick
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
    if (showAuthSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAuthSheet = false }
        ) {
            AccountBottomSheet(onDismiss = { showAuthSheet = false })
        }
    }
    selectedTask?.let { task ->
        ChangeProgressStatusDialog(
            taskId = task.id,
            currentStatus = task.progressStatus,
            onDismiss = { selectedTask = null },
            onSelect = { id, status -> vm.processCommand(TasksCommands.ChangeStatus(id, status)) }
        )
    }
}