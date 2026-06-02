package dev.stp.app.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import dev.stp.app.presentation.RegistrationScreen.RegistrationCommands
import dev.stp.app.presentation.RegistrationScreen.RegistrationState
import dev.stp.app.presentation.RegistrationScreen.RegistrationViewModel
import dev.stp.app.presentation.ui.theme.Typography

@Composable
fun RegistrationForm(
    state: RegistrationState.Content,
    viewModel: RegistrationViewModel,
    onBack: () -> Unit
) {
    Text(
        text = "Регистрация",
        style = Typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(bottom = 24.dp)
    )
    Spacer(modifier = Modifier.height(8.dp))

    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = state.login,
        onValueChange = { viewModel.process(RegistrationCommands.InputLogin(it)) },
        label = { Text("Придумайте логин") },
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
        onValueChange = { viewModel.process(RegistrationCommands.InputPassword(it)) },
        label = { Text("Придумайте пароль") },
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

    Button(
        modifier = Modifier.fillMaxWidth().height(50.dp),
        onClick = { viewModel.process(RegistrationCommands.Submit) },
        enabled = state.isSubmitEnabled,
        shape = MaterialTheme.shapes.medium
    ) {
        if (state.isSubmitting) {
            CircularProgressIndicator(color = Color.White)
        } else {
            Text("Зарегистрироваться")
        }
    }

    TextButton(
        onClick = onBack,
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Text("Уже есть аккаунт? Войти")
    }


}