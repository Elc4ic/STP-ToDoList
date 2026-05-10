package dev.stp.app.presentation.LogInScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.stp.app.presentation.components.LoginForm
import dev.stp.app.presentation.components.ProfileView
import org.koin.androidx.compose.koinViewModel

@Composable
fun LogInSheetContent(
    viewModel: LogInViewModel = koinViewModel(),
    onDismiss: () -> Unit,
    toRegistration: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val s = state) {
            is LogInState.Loading -> CircularProgressIndicator()

            is LogInState.Content -> {
                LoginForm(state = s, viewModel = viewModel, toRegistration = toRegistration)
            }

            is LogInState.Authorized -> {
                ProfileView(state = s, viewModel = viewModel)
            }
        }
    }
}