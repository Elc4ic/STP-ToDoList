package dev.stp.app.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.stp.app.presentation.LogInScreen.LogInCommands
import dev.stp.app.presentation.LogInScreen.LogInState
import dev.stp.app.presentation.LogInScreen.LogInViewModel

@Composable
fun ProfileView(
    state: LogInState.Authorized,
    viewModel: LogInViewModel
) {
    Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(64.dp))
    Text("Вы вошли как ${state.userName}", style = MaterialTheme.typography.titleLarge)

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = { viewModel.process(LogInCommands.Sync) },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
    ) {
        if (state.isSyncing) CircularProgressIndicator(color = Color.White)
        else Text("Синхронизировать с сервером")
    }

    OutlinedButton(
        onClick = { viewModel.process(LogInCommands.Logout) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Text("Выйти из аккаунта", color = MaterialTheme.colorScheme.error)
    }
}