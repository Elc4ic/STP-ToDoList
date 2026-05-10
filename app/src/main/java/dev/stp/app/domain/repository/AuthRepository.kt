package dev.stp.app.domain.repository

import dto.UserDto
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(login: String, password: String): Result<UserDto>
    suspend fun register(login: String, password: String): Result<UserDto>
    fun isAuthorized(): Flow<Boolean>
    suspend fun logout()
    fun loginName(): Flow<String?>
}