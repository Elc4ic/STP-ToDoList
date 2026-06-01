@file:OptIn(ExperimentalMaterial3Api::class)

package dev.stp.app.presentation.AddTaskScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import dev.stp.app.presentation.components.AppButton
import dev.stp.app.presentation.components.AppTextField
import dev.stp.app.presentation.components.DateField
import dev.stp.app.presentation.components.TaskDatePickerDialog
import org.koin.androidx.compose.koinViewModel
import ru.dedmos.todo.presentation.AddTaskScreen.AddCommands
import ru.dedmos.todo.presentation.AddTaskScreen.AddScreenEvent
import ru.dedmos.todo.presentation.AddTaskScreen.AddScreenState
import ru.dedmos.todo.presentation.AddTaskScreen.AddTaskViewModel


@Composable
fun AddTaskScreen(
    modifier: Modifier = Modifier,
    viewModel: AddTaskViewModel = koinViewModel(),
    onFinish: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateField by remember { mutableStateOf<SelectedDateField?>(null) }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                AddScreenEvent.Finish -> onFinish()
            }
        }
    }

    when (val state = state) {
        is AddScreenState.Loading -> {
            CircularProgressIndicator()
        }
        is AddScreenState.Creation -> {
            Scaffold(
                modifier = modifier,
                containerColor = MaterialTheme.colorScheme.primary,
                topBar = {
                    TopAppBar(
                        navigationIcon = {
                            Icon(
                                modifier = Modifier
                                    .padding(start = 16.dp, end = 8.dp)
                                    .clickable { onBack() },
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        },
                        title = { Text("") },
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
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        value = state.title,
                        onValueChange = { viewModel.processCommand(AddCommands.InputTitle(it)) },
                        placeholderText = "Title",
                        textStyle = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    )

                    DateField(
                        title = "Start date",
                        value = if (state.createdAt != 0L) {
                            formatDateFromMillis(state.createdAt)
                        } else "Select start date",
                        onClick = {
                            selectedDateField = SelectedDateField.START
                            showDatePicker = true
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DateField(
                        title = "End date",
                        value = if (state.deadline != 0L) {
                            formatDateFromMillis(state.deadline)
                        } else "Select end date",
                        onClick = {
                            selectedDateField = SelectedDateField.END
                            showDatePicker = true
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    AppTextField(
                        modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 8.dp),
                        value = state.content,
                        onValueChange = { viewModel.processCommand(AddCommands.InputContent(it)) },
                        placeholderText = "Content",
                        textStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.W400)
                    )

                    AppButton(
                        enabled = state.isSaveEnabled,
                        onClick = { viewModel.processCommand(AddCommands.Save) }
                    ){
                        Text("Добавить")
                    }
                }
            }

            if (showDatePicker) {
                TaskDatePickerDialog(
                    initialDateMillis = when (selectedDateField) {
                        SelectedDateField.START -> if (state.createdAt != 0L) state.createdAt else null
                        SelectedDateField.END -> if (state.deadline != 0L) state.deadline else null
                        null -> null
                    },
                    onDateSelected = { millis ->
                        millis?.let {
                            when (selectedDateField) {
                                SelectedDateField.START -> viewModel.processCommand(AddCommands.InputTimeStart(it))
                                SelectedDateField.END -> viewModel.processCommand(AddCommands.InputTimeEnd(it))
                                null -> Unit
                            }
                        }
                        showDatePicker = false
                    },
                    onDismiss = { showDatePicker = false }
                )
            }
        }
    }
}