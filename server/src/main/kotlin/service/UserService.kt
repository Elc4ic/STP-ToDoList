package dev.stp.service

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import dev.stp.domain.repository.UserRepository
import dev.stp.infrastructure.security.PasswordHasher
import dev.stp.infrastructure.security.TokenManager
import dto.AuthRequest
import dto.AuthResponse
import errors.AppError

interface UserService {
    suspend fun authenticate(request: AuthRequest): Either<AppError, AuthResponse>
    suspend fun register(request: AuthRequest): Either<AppError, AuthResponse>
    suspend fun refresh(refreshToken: String): Either<AppError, AuthResponse>
}

class UserServiceImpl(
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) : UserService {

    override suspend fun authenticate(request: AuthRequest): Either<AppError, AuthResponse> =
        either {
            val user = userRepository.findByLogin(request.login)
            ensureNotNull(user) { AppError.Auth.Server.InvalidCredentials() }

            ensure(PasswordHasher.eqHash(request.password, user.passwordHash)) {
                AppError.Auth.Server.InvalidCredentials()
            }

            val accessToken = tokenManager.generateAccessToken(user.id.toString(), user.login)
            val refreshToken = tokenManager.generateRefreshToken(user.id.toString())

            AuthResponse(accessToken, refreshToken, user.toDto())
        }

    override suspend fun register(request: AuthRequest): Either<AppError, AuthResponse> = either {
        val existingUser = userRepository.findByLogin(request.login)
        ensureNotNull(existingUser) { AppError.Auth.Server.UserAlreadyExists(request.login) }
        val user = userRepository.createUser(request)
        val accessToken = tokenManager.generateAccessToken(user.id, user.login)
        val refreshToken = tokenManager.generateRefreshToken(user.id)
        AuthResponse(accessToken, refreshToken, user)
    }

    override suspend fun refresh(refreshToken: String): Either<AppError, AuthResponse> = either {
        val userId = tokenManager.verifyRefreshToken(refreshToken)
        ensureNotNull(userId) { AppError.Auth.Server.InvalidToken() }

        val user = userRepository.findById(userId)
        ensureNotNull(user) { AppError.Auth.Server.InvalidToken() }

        val newAccess = tokenManager.generateAccessToken(user.id.toString(), user.login)
        val newRefresh = tokenManager.generateRefreshToken(user.id.toString())
        AuthResponse(newAccess, newRefresh, user.toDto())
    }
}