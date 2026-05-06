package dev.stp.app.presentation.AuthScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.stp.app.domain.repository.TaskRepository
import dev.stp.app.domain.usecases.AuthUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    data class Content(
        val login: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val error: String? = null,
        val isLoginEnabled: Boolean = false
    ) : AuthState()
}

sealed class AuthCommands {
    data class InputLogin(val value: String) : AuthCommands()
    data class InputPassword(val value: String) : AuthCommands()
    object Submit : AuthCommands()
}

class AuthViewModel(
    private val authUseCase : AuthUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState.Content())
    val state: StateFlow<AuthState.Content> = _state.asStateFlow()

    fun processCommand(command: AuthCommands) {
        when (command) {
            is AuthCommands.InputLogin -> {
                _state.value = _state.value.copy(
                    login = command.value,
                    isLoginEnabled = command.value.isNotBlank() && _state.value.password.isNotBlank()
                )
            }
            is AuthCommands.InputPassword -> {
                _state.value = _state.value.copy(
                    password = command.value,
                    isLoginEnabled = command.value.isNotBlank() && _state.value.login.isNotBlank()
                )
            }
            is AuthCommands.Submit -> {
                loginUser()
            }
        }
    }

    private fun loginUser() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            authUseCase(_state.value.login, _state.value.password)
            _state.value = _state.value.copy(isLoading = false)
        }
    }
}