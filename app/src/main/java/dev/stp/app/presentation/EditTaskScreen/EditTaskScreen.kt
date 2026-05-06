@file:OptIn(ExperimentalMaterial3Api::class)

package dev.stp.app.presentation.EditTaskScreen
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.stp.app.data.mapper.DateFormater.formatDateFromMillis
import dev.stp.app.data.mapper.SelectedDateField
import dev.stp.app.presentation.EditTaskScreen.EditCommands.InputTimeEnd
import dev.stp.app.presentation.EditTaskScreen.EditCommands.InputTimeStart
import dev.stp.app.presentation.components.ButtonComponent
import dev.stp.app.presentation.components.TextFieldComponent
import dev.stp.app.presentation.ui.theme.CustomIcons
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.dedmos.todo.presentation.AddTaskScreen.Commands
import java.util.UUID


@Composable
fun EditScreen(
    taskId: UUID,
    modifier: Modifier = Modifier,
    viewModel: EditTaskViewModel = koinViewModel {
        parametersOf(taskId)
    },
    onFinish: ()->Unit
){

    val state = viewModel.state.collectAsState()
    val currState = state.value
    var showDatePicker by remember {
        mutableStateOf(false)
    }

    var selectedDateField by remember {
        mutableStateOf<SelectedDateField?>(null)
    }

    var startDateMillis by remember {
        mutableStateOf<Long?>(null)
    }

    var endDateMillis by remember {
        mutableStateOf<Long?>(null)
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                ScreenEvent.Finish -> onFinish()
            }
        }
    }

    when(currState) {
        is ScreenState.Loading -> {
            CircularProgressIndicator()
        }
        is ScreenState.Editing -> {
            LaunchedEffect(taskId) {
                startDateMillis = currState.task.createdAt
                endDateMillis = currState.task.deadline

            }
            Scaffold(
                modifier = modifier,
                containerColor = MaterialTheme.colorScheme.primary,
                topBar = {
                    TopAppBar(
                        actions = {
                            if(currState.task.isPinned){
                                Icon(
                                    modifier = Modifier
                                        .padding(end = 32.dp)
                                        .clickable{
                                            viewModel.processCommands(EditCommands.SwitchPinned)
                                        },
                                    imageVector = CustomIcons.Pinned,
                                    contentDescription = "",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }else{
                                Icon(
                                    modifier = Modifier
                                        .padding(end = 32.dp)
                                        .clickable{
                                            viewModel.processCommands(EditCommands.SwitchPinned)
                                        },
                                    imageVector = CustomIcons.UnPinned,
                                    contentDescription = "",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }


                            Icon(
                                modifier = Modifier
                                    .padding(end = 32.dp)
                                    .clickable{
                                    viewModel.processCommands(EditCommands.DeleteTask)
                                    onFinish()
                                },
                                imageVector = Icons.Default.Delete,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        navigationIcon = {
                            Icon(
                                modifier = Modifier
                                    .padding(start = 16.dp, end = 8.dp)
                                    .clickable {
                                        onFinish()
                                    },
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        },
                        title = {
                            Text(
                                text = ""
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )

                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    TextFieldComponent(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        value = currState.task.title,
                        onValueChange = { viewModel.processCommand(Commands.InputTitle(it)) },
                        placeholderText = "Title",
                        textStyle = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    DateField(
                        title = "Start date",
                        value = startDateMillis?.let {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                formatDateFromMillis(it)
                            } else {
                                TODO("VERSION.SDK_INT < O")
                            }
                        } ?: "Select start date",
                        onClick = {
                            selectedDateField = SelectedDateField.START
                            showDatePicker = true
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DateField(
                        title = "End date",
                        value = endDateMillis?.let {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                formatDateFromMillis(it)
                            } else {
                                TODO("VERSION.SDK_INT < O")
                            }
                        } ?: "Select end date",
                        onClick = {
                            selectedDateField = SelectedDateField.END
                            showDatePicker = true
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    TextFieldComponent(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        value = currState.task.content,
                        onValueChange = { viewModel.processCommand(Commands.InputContent(it)) },
                        placeholderText = "Content",
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W400
                        )
                    )
                    ButtonComponent(
                        isEnabled = currState.isSaveEnabled,
                        onClick = {
                            viewModel.processCommand(Commands.Save)
                            onFinish()
                        }
                    )
                }

            }
            if (showDatePicker) {
                TaskDatePickerDialog(
                    initialDateMillis = when (selectedDateField) {
                        SelectedDateField.START -> startDateMillis
                        SelectedDateField.END -> endDateMillis
                        null -> null
                    },
                    onDateSelected = { millis ->
                        when (selectedDateField) {
                            SelectedDateField.START -> {
                                startDateMillis = millis
                                viewModel.processCommands(InputTimeStart(millis ?: 0L))
                            }

                            SelectedDateField.END -> {
                                endDateMillis = millis
                                viewModel.processCommands(InputTimeEnd(millis ?: 0L))
                            }

                            null -> Unit
                        }
                    },
                    onDismiss = {
                        showDatePicker = false
                    }
                )
            }
        }
    }
}



@Composable
private fun DateField(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clickable {
                onClick()
            },
        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.12f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskDatePickerDialog(
    initialDateMillis: Long?,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis
    )

    DatePickerDialog(
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}