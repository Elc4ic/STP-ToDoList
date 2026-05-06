@file:OptIn(ExperimentalMaterial3Api::class)

package dev.stp.app.presentation.TasksScreen


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.stp.app.R
import dev.stp.app.domain.entity.Task
import dev.stp.app.presentation.components.SearchBar
import dev.stp.app.presentation.components.SwitchScreen
import dev.stp.app.presentation.components.TaskCard
import org.koin.androidx.compose.koinViewModel


@Composable
fun TasksScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = koinViewModel(),
    onTaskClick: (Task) -> Unit,
    addTaskClick: () -> Unit,
    notifyClick: () -> Unit,
    settingsClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

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
                    Icon(
                        modifier = Modifier.clickable {
                            notifyClick()
                        },
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onPrimary

                    )
                    Spacer(modifier = Modifier.width(24.dp))
                    Icon(
                        modifier = Modifier.clickable {
                            settingsClick()
                        },
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
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
                SwitchScreen()
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                SearchBar(
                    query = state.query
                ) { viewModel.processCommand(TasksCommands.InputQuery(it)) }
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
            state.pinnedTasks.forEach {
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
}