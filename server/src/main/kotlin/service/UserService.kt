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
import errors.IError
import validator.AuthValidator

interface UserService {
    suspend fun authenticate(request: AuthRequest): Either<IError, AuthResponse>
    suspend fun register(request: AuthRequest): Either<IError, AuthResponse>
    suspend fun refresh(refreshToken: String): Either<IError, AuthResponse>
}

class UserServiceImpl(
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) : UserService {

    override suspend fun authenticate(request: AuthRequest): Either<IError, AuthResponse> =
        either {
            val (validLogin, validPassword) = AuthValidator.validateCredentials(
                request.login,
                request.password
            ).bind()

            val user = userRepository.findByLogin(validLogin)
            ensureNotNull(user) { IError.Auth.InvalidCredentials() }

            ensure(PasswordHasher.eqHash(validPassword, user.passwordHash)) {
                IError.Auth.InvalidCredentials()
            }

            val accessToken = tokenManager.generateAccessToken(user.id.toString(), user.login)
            val refreshToken = tokenManager.generateRefreshToken(user.id.toString())

            AuthResponse(accessToken, refreshToken, user.toDto())
        }

    override suspend fun register(request: AuthRequest): Either<IError, AuthResponse> = either {
        val (validLogin, validPassword) = AuthValidator.validateCredentials(
            request.login,
            request.password
        ).bind()

        val existingUser = userRepository.findByLogin(validLogin)
        ensure(existingUser == null) { IError.Auth.UserAlreadyExists(validLogin) }

        val user = userRepository.createUser(validLogin, validPassword)
        val accessToken = tokenManager.generateAccessToken(user.id.toString(), user.login)
        val refreshToken = tokenManager.generateRefreshToken(user.id.toString())
        AuthResponse(accessToken, refreshToken, user.toDto())
    }

    override suspend fun refresh(refreshToken: String): Either<IError, AuthResponse> = either {
        val userId = tokenManager.verifyRefreshToken(refreshToken)
        ensureNotNull(userId) { IError.Auth.InvalidToken() }

        val user = userRepository.findById(userId)
        ensureNotNull(user) { IError.Auth.InvalidToken() }

        val newAccess = tokenManager.generateAccessToken(user.id.toString(), user.login)
        val newRefresh = tokenManager.generateRefreshToken(user.id.toString())
        AuthResponse(newAccess, newRefresh, user.toDto())
    }
}