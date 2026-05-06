@file:OptIn(ExperimentalMaterial3Api::class)

package dev.stp.app.presentation.EditTaskScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import dev.stp.app.presentation.components.AppButton
import dev.stp.app.presentation.components.AppTextField
import dev.stp.app.presentation.components.DateField
import dev.stp.app.presentation.components.TaskDatePickerDialog
import dev.stp.app.presentation.ui.theme.CustomIcons
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.dedmos.todo.presentation.AddTaskScreen.AddCommands
import java.util.UUID


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditScreen(
    taskId: UUID,
    modifier: Modifier = Modifier,
    viewModel: EditTaskViewModel = koinViewModel {
        parametersOf(taskId)
    },
    onFinish: () -> Unit
) {

    val state by viewModel.state.collectAsState()

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    var selectedDateField by remember {
        mutableStateOf<SelectedDateField?>(null)
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                EditScreenEvent.Finish -> onFinish()
            }
        }
    }

    when (val state = state) {
        is EditScreenState.Loading -> {
            CircularProgressIndicator()
        }

        is EditScreenState.Editing -> {
            Scaffold(
                modifier = modifier,
                containerColor = MaterialTheme.colorScheme.primary,
                topBar = {
                    TopAppBar(
                        actions = {
                            if (state.task.isPinned) {
                                Icon(
                                    modifier = Modifier
                                        .padding(end = 32.dp)
                                        .clickable {
                                            viewModel.processCommands(EditCommands.SwitchPinned)
                                        },
                                    imageVector = CustomIcons.Pinned,
                                    contentDescription = "",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Icon(
                                    modifier = Modifier
                                        .padding(end = 32.dp)
                                        .clickable {
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
                                    .clickable {
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
                    AppTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        value = state.task.title,
                        onValueChange = { viewModel.processCommands(EditCommands.InputTitle(it)) },
                        placeholderText = "Title",
                        textStyle = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    DateField(
                        title = "Start date",
                        value = if (state.task.createdAt != 0L) {
                            formatDateFromMillis(state.task.createdAt)
                        } else "Select start date",
                        onClick = {
                            selectedDateField = SelectedDateField.START
                            showDatePicker = true
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DateField(
                        title = "End date",
                        value = if (state.task.deadline != 0L) {
                            formatDateFromMillis(state.task.deadline)
                        } else "Select end date",
                        onClick = {
                            selectedDateField = SelectedDateField.END
                            showDatePicker = true
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    AppTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        value = state.task.content,
                        onValueChange = { viewModel.processCommands(EditCommands.InputContent(it)) },
                        placeholderText = "Content",
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W400
                        )
                    )
                    AppButton(
                        isEnabled = state.isSaveEnabled,
                        onClick = {
                            viewModel.processCommands(EditCommands.Save)
                            onFinish()
                        }
                    )
                }

            }
            if (showDatePicker) {
                TaskDatePickerDialog(
                    initialDateMillis = when (selectedDateField) {
                        SelectedDateField.START -> if (state.task.createdAt != 0L) state.task.createdAt else null
                        SelectedDateField.END -> if (state.task.deadline != 0L) state.task.deadline else null
                        null -> null
                    },
                    onDateSelected = { millis ->
                        millis?.let {
                            when (selectedDateField) {
                                SelectedDateField.START -> viewModel.processCommands(
                                    EditCommands.InputTimeStart(
                                        it
                                    )
                                )

                                SelectedDateField.END -> viewModel.processCommands(
                                    EditCommands.InputTimeEnd(
                                        it
                                    )
                                )

                                null -> Unit
                            }
                        }
                        showDatePicker = true
                    },
                    onDismiss = {
                        showDatePicker = false
                    }
                )
            }
        }
    }
}