package dev.stp.service

import dev.stp.domain.repository.UserRepository
import dev.stp.infrastructure.security.PasswordHasher
import dev.stp.infrastructure.security.TokenManager
import dto.AuthRequest
import dto.AuthResponse
import dto.UserDto

interface UserService {
    suspend fun authenticate(request: AuthRequest): AuthResponse?
    suspend fun register(request: AuthRequest): UserDto
}

class UserServiceImpl(
    private val repository: UserRepository,
    private val tokenManager: TokenManager
) : UserService {

    override suspend fun authenticate(request: AuthRequest): AuthResponse? {
        val user = repository.findUserByLogin(request.login) ?: return null

        return if (PasswordHasher.eqHash(request.password, user.passwordHash)) {
            val token = tokenManager.generateToken(user.login)
            AuthResponse(token = token, user = user.toDto())
        } else null
    }

    override suspend fun register(request: AuthRequest): UserDto {
        // TODO проверить занят ли логин
        return repository.createUser(request)
    }
}