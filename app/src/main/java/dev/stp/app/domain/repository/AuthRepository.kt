package dev.stp.app.domain.repository

import dto.UserDto

interface AuthRepository {
    suspend fun login(login: String, password: String): Result<UserDto>
    suspend fun register(login: String, password: String): Result<UserDto>
    suspend fun logout()
}