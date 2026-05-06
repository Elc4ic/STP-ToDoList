package dev.stp.app.presentation.LogInScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.stp.app.domain.usecases.LogInUseCase
import errors.AppError
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface LogInState {
    data object Loading : LogInState

    data class Content(
        val login: String = "",
        val loginError: String? = null,
        val password: String = "",
        val passwordError: String? = null,
        val generalError: String? = null,
        val isSubmitting: Boolean = false
    ) : LogInState {
        val isSubmitEnabled: Boolean
            get() = login.isNotBlank() && password.isNotBlank() && !isSubmitting
    }
}
sealed interface LogInCommands {
    data class InputLogin(val value: String) : LogInCommands
    data class InputPassword(val value: String) : LogInCommands
    data object Submit : LogInCommands
}

sealed interface LogInEvent {
    data object NavigateToHome : LogInEvent
}

class LogInViewModel(
    private val logInUseCase: LogInUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<LogInState>(LogInState.Content())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<LogInEvent>()
    val event = _event.asSharedFlow()

    fun processCommand(command: LogInCommands) {
        when (command) {
            is LogInCommands.InputLogin -> updateContent { it.copy(login = command.value, loginError = null) }
            is LogInCommands.InputPassword -> updateContent { it.copy(password = command.value, passwordError = null) }
            LogInCommands.Submit -> login()
        }
    }

    private inline fun updateContent(crossinline update: (LogInState.Content) -> LogInState.Content) {
        _state.update { prevState ->
            if (prevState is LogInState.Content) update(prevState) else prevState
        }
    }

    private fun login() {
        val currentState = _state.value as? LogInState.Content ?: return
        if (!currentState.isSubmitEnabled) return

        viewModelScope.launch {
            updateContent { it.copy(isSubmitting = true, generalError = null) }

            val result = logInUseCase(currentState.login, currentState.password)

            result.onSuccess {
                _event.emit(LogInEvent.NavigateToHome)
            }.onFailure { throwable ->
                val error = throwable as? AppError
                handleError(error)
            }

            updateContent { it.copy(isSubmitting = false) }
        }
    }

    private fun handleError(error: AppError?) {
        updateContent { prevState ->
            when (error) {
                is AppError.Auth.LoginFieldEmpty -> prevState.copy(loginError = error.message)
                is AppError.Auth.PasswordFieldEmpty -> prevState.copy(loginError = error.message)
                is AppError.Auth.PasswordTooShort -> prevState.copy(passwordError = error.message)
                is AppError.Auth.InvalidCredentials -> prevState.copy(generalError = error.message)
                is AppError.NetworkError -> prevState.copy(generalError = error.message)
                else -> prevState.copy(generalError = error?.message ?: "Неизвестная ошибка")
            }
        }
    }
}