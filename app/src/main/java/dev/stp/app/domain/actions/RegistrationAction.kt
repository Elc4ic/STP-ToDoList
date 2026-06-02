package dev.stp.app.domain.actions

import arrow.core.Either
import arrow.core.raise.context.bind
import arrow.core.raise.context.either
import dev.stp.app.domain.repository.AuthRepository
import dto.UserDto
import errors.IError
import validator.AuthValidator

context(repo: AuthRepository)
suspend fun registrate(login: String, password: String): Either<IError, UserDto> = either {
    val (clearLogin, clearPassword) = AuthValidator.validateCredentials(login, password).bind()
    repo.register(clearLogin, clearPassword).bind()
}