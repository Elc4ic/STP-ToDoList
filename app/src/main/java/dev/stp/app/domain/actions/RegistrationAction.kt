package dev.stp.app.domain.actions

import arrow.core.Either
import arrow.core.raise.context.bind
import arrow.core.raise.context.either
import arrow.core.raise.context.ensure
import dev.stp.app.domain.repository.AuthRepository
import dto.UserDto
import errors.AppError

context(repo: AuthRepository)
suspend fun registrate(login: String, password: String): Either<AppError, UserDto> = either {
    ensure(login.isBlank()) { AppError.Client.Auth.LoginFieldEmpty() }
    ensure(password.isBlank()) { AppError.Client.Auth.PasswordFieldEmpty() }
    ensure(password.length < 6) { AppError.Client.Auth.PasswordTooShort() }
    repo.register(login, password).bind()
}