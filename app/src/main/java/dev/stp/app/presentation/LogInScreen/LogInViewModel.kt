package dev.stp.app.presentation.LogInScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.getOrElse
import dev.stp.app.domain.actions.logIn
import dev.stp.app.domain.repository.AuthRepository
import dev.stp.app.domain.repository.SyncRepository
import errors.AppError
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
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

    data class Authorized(
        val userName: String,
        val isSyncing: Boolean = false
    ) : LogInState
}

sealed interface LogInCommands {
    data class InputLogin(val value: String) : LogInCommands
    data class InputPassword(val value: String) : LogInCommands
    data object Submit : LogInCommands
    data object Logout : LogInCommands
    data object Sync : LogInCommands

    data object Pull : LogInCommands
}

sealed interface LogInEvent {
    data object NavigateToHome : LogInEvent
}

class LogInViewModel(
    private val authRepository: AuthRepository,
    private val syncRepository: SyncRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<LogInState>(LogInState.Content())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<LogInEvent>()

    init {
        viewModelScope.launch {
            authRepository.isAuthorized().collect { isAuthed ->
                if (isAuthed) {
                    authRepository.loginName().collect {
                        _state.value = LogInState.Authorized(userName = it.getOrElse { "User" })
                    }
                } else {
                    _state.value = LogInState.Content()
                }
            }
        }
    }

    fun process(command: LogInCommands) {
        when (command) {
            is LogInCommands.InputLogin -> updateContent {
                it.copy(
                    login = command.value,
                    loginError = null
                )
            }

            is LogInCommands.InputPassword -> updateContent {
                it.copy(
                    password = command.value,
                    passwordError = null
                )
            }

            LogInCommands.Submit -> login()
            LogInCommands.Logout -> viewModelScope.launch { authRepository.logout() }
            LogInCommands.Sync -> syncData()
            LogInCommands.Pull -> pullUp()
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
            with(authRepository) {
                updateContent { it.copy(isSubmitting = true, generalError = null) }

                val result = logIn(currentState.login, currentState.password)

                result.fold(
                    ifLeft = { error -> handleError(error) },
                    ifRight = { _event.emit(LogInEvent.NavigateToHome) }
                )
                updateContent { it.copy(isSubmitting = false) }
            }
        }
    }

    private fun syncData() {
        val current = _state.value as? LogInState.Authorized ?: return
        viewModelScope.launch {
            _state.value = current.copy(isSyncing = true)
            syncRepository.trySync()
            _state.value = current.copy(isSyncing = false)
        }
    }

    private fun pullUp() {
        val current = _state.value as? LogInState.Authorized ?: return
        viewModelScope.launch {
            _state.value = current.copy(isSyncing = true)
            syncRepository.getFromServer().fold(
                ifLeft = { error -> handleError(error) },
                ifRight = {}
            )
            _state.value = current.copy(isSyncing = false)
        }
    }

    private fun handleError(error: AppError?) {
        updateContent { prevState ->
            when (error) {
                is AppError.Client.Auth.LoginFieldEmpty -> prevState.copy(loginError = error.message)
                is AppError.Client.Auth.PasswordFieldEmpty -> prevState.copy(loginError = error.message)
                is AppError.Client.Auth.PasswordTooShort -> prevState.copy(passwordError = error.message)
                is AppError.Server.Auth.InvalidCredentials -> prevState.copy(generalError = error.message)
                is AppError.Server.Auth.UserNotFound -> prevState.copy(generalError = error.message)
                is AppError.NetworkError -> prevState.copy(generalError = error.message)
                else -> prevState.copy(generalError = error?.message ?: "Неизвестная ошибка")
            }
        }
    }
}