package dev.stp.app.domain.repository

import arrow.core.Either
import arrow.core.Option
import dto.UserDto
import errors.IError
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(login: String, password: String): Either<IError, UserDto>
    suspend fun register(login: String, password: String): Either<IError, UserDto>
    fun isAuthorized(): Flow<Boolean>
    suspend fun logout()
    fun loginName(): Flow<Option<String>>
}