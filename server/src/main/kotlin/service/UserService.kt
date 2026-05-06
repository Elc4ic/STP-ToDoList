package dev.stp.service

import dev.stp.domain.repository.UserRepository
import dev.stp.infrastructure.security.PasswordHasher
import dev.stp.infrastructure.security.TokenManager
import dto.AuthRequest
import dto.AuthResponse
import dto.UserDto
import errors.AppError

interface UserService {
    suspend fun authenticate(request: AuthRequest): AuthResponse
    suspend fun register(request: AuthRequest): UserDto
    suspend fun refresh(refreshToken: String): AuthResponse?
}

class UserServiceImpl(
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) : UserService {

    override suspend fun authenticate(request: AuthRequest): AuthResponse {
        val user = userRepository.findByLogin(request.login)
            ?: throw AppError.Auth.InvalidCredentials()

        if (!PasswordHasher.eqHash(request.password, user.passwordHash)) {
            throw AppError.Auth.InvalidCredentials()
        }

        val accessToken = tokenManager.generateAccessToken(user.id.toString(), user.login)
        val refreshToken = tokenManager.generateRefreshToken(user.id.toString())

        return AuthResponse(accessToken, refreshToken, user.toDto())
    }

    override suspend fun register(request: AuthRequest): UserDto {
        val existingUser = userRepository.findByLogin(request.login)
        if (existingUser != null) {
            throw AppError.Auth.UserAlreadyExists(request.login)
        }

        return userRepository.createUser(request)
    }

    override suspend fun refresh(refreshToken: String): AuthResponse? {
        val userId = tokenManager.verifyRefreshToken(refreshToken)

        return if (userId != null) {
            val user = userRepository.findById(userId)
            if (user != null) {
                val newAccess = tokenManager.generateAccessToken(user.id.toString(), user.login)
                val newRefresh = tokenManager.generateRefreshToken(user.id.toString())
                AuthResponse(newAccess, newRefresh, user.toDto())
            } else null
        } else null
    }
}