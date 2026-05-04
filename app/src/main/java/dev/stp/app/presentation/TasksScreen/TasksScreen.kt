@file:OptIn(ExperimentalMaterial3Api::class)

package dev.stp.app.presentation.TasksScreen


import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.stp.app.R
import dev.stp.app.data.mapper.DateFormater
import dev.stp.app.domain.entity.Task
import dev.stp.app.presentation.ui.theme.CustomIcons
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.compose.koinViewModel



@Composable
fun TasksScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = koinViewModel(),
    onTaskClick: (Task)->Unit,
    addTaskClick: ()->Unit,
    notifyClick: ()->Unit,
    settingsClick: ()->Unit
    ){
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        floatingActionButton = {
            FloatingActionButton(
            onClick =  addTaskClick,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add task",
            )

        }},
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
                        modifier = Modifier.clickable{
                            notifyClick()
                        },
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onPrimary

                    )
                    Spacer(modifier = Modifier.width(24.dp))
                    Icon(
                        modifier = Modifier.clickable{
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

    )

    {innerPadding->
        LazyColumn(
            contentPadding = innerPadding) {
            item{
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                SwitchScreen()
            }
            item{
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                SearchBar(
                    query = state.query
                ) {viewModel.processCommand(TasksCommands.InputQuery(it)) }
            }
            item{
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


@Composable
private fun SwitchScreen(
    modifier: Modifier = Modifier

){
    Row(
        modifier = modifier.fillMaxWidth()
            .padding(horizontal = 58.dp)
        ,
        horizontalArrangement = Arrangement.spacedBy(40.dp),
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center

        ) {
            Text(
                text = "Schedule",
                fontWeight = FontWeight.W600,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Box (
            modifier = Modifier.clip(
                RoundedCornerShape(4.dp)
            ).background(color= MaterialTheme.colorScheme.onSurface).weight(1f).padding(vertical = 1.dp),
            contentAlignment = Alignment.Center

        ){
            Text(
                text = "Note",
                color = MaterialTheme.colorScheme.onPrimary

            )
        }


    }

}

@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit
){
    TextField(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth(),
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = "Search Note",
                fontSize = 16.sp,
                color =MaterialTheme.colorScheme.onSurface,

            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.secondary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        leadingIcon = {
            Icon(
                modifier = Modifier.padding(start = 16.dp),
                imageVector = Icons.Default.Search,
                contentDescription = "search",
                tint = MaterialTheme.colorScheme.onSurface
            )
        },
        shape = RoundedCornerShape(30.dp)
    )
}



@Composable
private fun TaskCard(
    modifier: Modifier = Modifier,
    onTaskClick: (Task) -> Unit,
    task: Task,
    onLongClick : (Task) ->Unit
){

    Column(
        modifier = modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick ={
                    onTaskClick(task)
                },
                onLongClick = {
                    onLongClick(task)
                }
            )
            .clip(RoundedCornerShape(8.dp))
            .background(color = MaterialTheme.colorScheme.primary )
            .padding(16.dp)

    ) {
        Text(
            text = task.title,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = task.content,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.W400,
            fontSize = 14.sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Text(
                    text = "${DateFormater.formatDateFromMillis(task.createdAt)} - ${DateFormater.formatDateFromMillis(task.deadline)} ",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.W400,
                    fontSize = 10.sp
                )
            }
            if(task.isPinned){
                Icon(
                    imageVector = CustomIcons.Pinned,
                    contentDescription = "pinned",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

        }



    }



}


