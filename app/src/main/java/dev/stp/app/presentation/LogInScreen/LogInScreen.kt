@file:OptIn(ExperimentalMaterial3Api::class)

package dev.stp.app.presentation.LogInScreen

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel

@Composable
fun LogInScreen(
    viewModel: LogInViewModel = koinViewModel(),
    onSuccess: () -> Unit,
    toRegistration: ()->Unit,
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
                topBar = {
                    TopAppBar(
                        modifier = Modifier.fillMaxWidth(),
                        title = {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "on.time",
                                fontSize = 50.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    )
                },
                containerColor = MaterialTheme.colorScheme.surface
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
                            text = "Вход",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(bottom = 32.dp)
                        )
                        TextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = state.login,
                            onValueChange ={ viewModel.processCommand(LogInCommands.InputLogin(it)) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                            placeholder = {
                                Text(
                                    text = "Login",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Medium,
                                    fontSize =  18.sp,
                                )
                            },
                            textStyle = TextStyle(color = MaterialTheme.colorScheme.onPrimary)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = state.password,
                            onValueChange = { viewModel.processCommand(LogInCommands.InputPassword(it)) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                            placeholder = {
                                Text(
                                    text = "Password",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Medium,
                                    fontSize =  18.sp,
                                )
                            },
                            textStyle = TextStyle(color = MaterialTheme.colorScheme.onPrimary)
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
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text("Войти")
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            modifier = Modifier.clickable(
                                onClick = toRegistration
                            ),
                            text = "Нет аккаунта? Зарегистрируйтесь",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 10.sp
                        )

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