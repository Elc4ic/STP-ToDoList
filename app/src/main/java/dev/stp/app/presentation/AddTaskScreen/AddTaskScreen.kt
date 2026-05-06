@file:OptIn(ExperimentalMaterial3Api::class)

package dev.stp.app.presentation.AddTaskScreen

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import dev.stp.app.presentation.components.ButtonComponent
import dev.stp.app.presentation.components.DateField
import dev.stp.app.presentation.components.TaskDatePickerDialog
import dev.stp.app.presentation.components.TextFieldComponent
import org.koin.androidx.compose.koinViewModel
import ru.dedmos.todo.presentation.AddTaskScreen.AddTaskState
import ru.dedmos.todo.presentation.AddTaskScreen.AddTaskViewModel
import ru.dedmos.todo.presentation.AddTaskScreen.Commands


@Composable
fun AddTaskScreen(
    modifier: Modifier = Modifier,
    viewModel: AddTaskViewModel = koinViewModel(),
    onFinish: () -> Unit,
    onBack: () -> Unit
) {

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

    when (currState) {
        is AddTaskState.Creation -> {
            Scaffold(
                modifier = modifier,
                containerColor = MaterialTheme.colorScheme.primary,
                topBar = {
                    TopAppBar(
                        navigationIcon = {
                            Icon(
                                modifier = Modifier
                                    .padding(start = 16.dp, end = 8.dp)
                                    .clickable {
                                        onBack()
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
                        value = currState.title,
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
                        value = currState.content,
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
                                viewModel.processCommand(Commands.InputTimeStart(millis ?: 0L))
                            }

                            SelectedDateField.END -> {
                                endDateMillis = millis
                                viewModel.processCommand(Commands.InputTimeEnd(millis ?: 0L))
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