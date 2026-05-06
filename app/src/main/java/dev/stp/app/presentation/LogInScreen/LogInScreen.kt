package dev.stp.app.presentation.LogInScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.stp.app.presentation.components.AppTextField
import org.koin.androidx.compose.koinViewModel

@Composable
fun LogInScreen(
    viewModel: LogInViewModel = koinViewModel(),
    onSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsState()


    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                LogInEvent.NavigateToHome -> onSuccess()
            }
        }
    }

    when (val state = state) {
        is LogInState.Loading -> {
            CircularProgressIndicator()
        }

        is LogInState.Content -> {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.primary
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Приветствуем",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    AppTextField(
                        value = state.login,
                        onValueChange = { viewModel.processCommand(LogInCommands.InputLogin(it)) },
                        placeholderText = "Login",
                        textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AppTextField(
                        value = state.password,
                        onValueChange = { viewModel.processCommand(LogInCommands.InputPassword(it)) },
                        placeholderText = "Password",
                        textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        onClick = { viewModel.processCommand(LogInCommands.Submit) },
                        enabled = state.isSubmitEnabled,
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Войти")
                    }

                    state.generalError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
            }
        }
    }
}