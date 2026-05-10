package dev.stp.app.data.repository

import apiRoutes.Api
import dev.stp.app.data.datasource.TokenManager
import dev.stp.app.domain.repository.AuthRepository
import dto.AuthRequest
import dto.AuthResponse
import dto.UserDto
import errors.AppError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.net.UnknownHostException

class AuthRepositoryImpl(
    private val client: HttpClient,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(login: String, password: String): Result<UserDto> {
        return try {
            val response = client.post(Api.Auth.Login.url()) {
                setBody(AuthRequest(login, password))
            }

            when (response.status) {
                HttpStatusCode.OK -> {
                    val authResponse = response.body<AuthResponse>()
                    tokenManager.saveTokens(
                        authResponse.user.login,
                        authResponse.accessToken,
                        authResponse.refreshToken
                    )
                    Result.success(authResponse.user)
                }

                HttpStatusCode.NotFound -> AppError.Auth.Server.UserNotFound().toResult()
                HttpStatusCode.Unauthorized -> AppError.Auth.Server.InvalidCredentials().toResult()
                else -> AppError.ServerError().toResult()
            }
        } catch (e: Exception) {
            val mappedError = when (e) {
                is ConnectTimeoutException,
                is UnknownHostException -> AppError.NetworkError()

                else -> AppError.Unknown(e.message ?: "Unknown error")
            }
            mappedError.toResult()
        }
    }

    override suspend fun register(login: String, password: String): Result<UserDto> {
        return try {
            val response = client.post(Api.Auth.Register.url()) {
                setBody(AuthRequest(login, password))
            }

            when (response.status) {
                HttpStatusCode.Created -> {
                    val authResponse = response.body<AuthResponse>()
                    tokenManager.saveTokens(
                        authResponse.user.login,
                        authResponse.accessToken,
                        authResponse.refreshToken
                    )
                    Result.success(authResponse.user)
                }

                HttpStatusCode.Unauthorized -> AppError.Auth.Server.InvalidCredentials().toResult()
                HttpStatusCode.Conflict -> AppError.Auth.Server.UserAlreadyExists(login).toResult()
                else -> AppError.ServerError().toResult()
            }
        } catch (e: Exception) {
            val mappedError = when (e) {
                is ConnectTimeoutException,
                is UnknownHostException -> AppError.NetworkError()

                else -> AppError.Unknown(e.message ?: "Неизвестная ошибка")
            }
            mappedError.toResult()
        }
    }

    override fun isAuthorized(): Flow<Boolean> = tokenManager.accessToken.map { it != null }

    override fun loginName(): Flow<String?> = tokenManager.login

    override suspend fun logout() {
        tokenManager.clear()
    }
}