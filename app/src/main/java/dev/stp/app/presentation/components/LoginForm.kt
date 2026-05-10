package dev.stp.app.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.stp.app.presentation.LogInScreen.LogInCommands
import dev.stp.app.presentation.LogInScreen.LogInState
import dev.stp.app.presentation.LogInScreen.LogInViewModel
import dev.stp.app.presentation.RegistrationScreen.RegistrationCommands
import dev.stp.app.presentation.ui.theme.Typography

@Composable
fun LoginForm(
    state: LogInState.Content,
    viewModel: LogInViewModel,
    toRegistration: () -> Unit
) {
    Text("Вход", style = Typography.bodyLarge)

    Spacer(modifier = Modifier.height(8.dp))

    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = state.login,
        onValueChange = { viewModel.process(LogInCommands.InputLogin(it)) },
        label = { Text("Логин") },
        isError = state.loginError != null,
        supportingText = {
            state.loginError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        )
    )

    Spacer(modifier = Modifier.height(8.dp))

    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = state.password,
        onValueChange = { viewModel.process(LogInCommands.InputPassword(it)) },
        label = { Text("Пароль") },
        isError = state.passwordError != null,
        supportingText = {
            state.passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        )
    )

    Spacer(modifier = Modifier.height(8.dp))
    state.generalError?.let {
        Text(
            text = it,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
    Spacer(modifier = Modifier.height(16.dp))

    AppButton(
        onClick = { viewModel.process(LogInCommands.Submit) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        enabled = !state.isSubmitting && state.isSubmitEnabled
    ) {
        if (state.isSubmitting) CircularProgressIndicator()
        else Text("Войти")
    }

    TextButton(onClick = toRegistration) {
        Text("Нет аккаунта? Зарегистрируйтесь")
    }
}