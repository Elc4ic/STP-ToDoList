@file:OptIn(ExperimentalMaterial3Api::class)

package dev.stp.app.presentation.AuthScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import dev.stp.app.presentation.components.TextFieldComponent
import dev.stp.app.presentation.components.ButtonComponent

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = koinViewModel(),
    onSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsState()

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
                text = "Welcome",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            TextFieldComponent(
                value = state.login,
                onValueChange = { viewModel.processCommand(AuthCommands.InputLogin(it)) },
                placeholderText = "Login",
                textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextFieldComponent(
                value = state.password,
                onValueChange = { viewModel.processCommand(AuthCommands.InputPassword(it)) },
                placeholderText = "Password",
                textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (state.isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Button(
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    onClick = { viewModel.processCommand(AuthCommands.Submit) },
                    enabled = state.isLoginEnabled,
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Sign In")
                }
            }

            state.error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}