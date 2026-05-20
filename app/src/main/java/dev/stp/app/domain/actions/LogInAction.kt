package dev.stp.app.domain.actions

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import dev.stp.app.domain.repository.AuthRepository
import dto.UserDto
import errors.AppError


context(repo: AuthRepository)
suspend fun logIn(login: String, password: String): Either<AppError, UserDto> = either {
    ensure(login.isNotBlank()) { AppError.Client.Auth.LoginFieldEmpty() }
    ensure(password.isNotBlank()) { AppError.Client.Auth.PasswordFieldEmpty() }

    repo.login(login, password).bind()
}