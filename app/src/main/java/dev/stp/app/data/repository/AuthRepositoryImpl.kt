package dev.stp.app.data.repository

import apiRoutes.Api
import dev.stp.app.data.datasource.TokenManager
import dev.stp.app.data.localDB.TaskDao
import dev.stp.app.data.mapper.safeApiCall
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
    private val taskDao: TaskDao,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(login: String, password: String): Result<UserDto> {
        return safeApiCall<AuthResponse>(
            call = {
                client.post(Api.Auth.Login.url()) {
                    setBody(AuthRequest(login, password))
                }
            },
            mapError = { status ->
                when (status) {
                    HttpStatusCode.NotFound -> AppError.Auth.Server.UserNotFound()
                    else -> null
                }
            }
        ).map { response ->
            tokenManager.saveTokens(
                response.user,
                response.accessToken,
                response.refreshToken
            )
            response.user
        }
    }

    override suspend fun register(login: String, password: String): Result<UserDto> {
        return safeApiCall<AuthResponse>(
            call = {
                client.post(Api.Auth.Register.url()) {
                    setBody(AuthRequest(login, password))
                }
            },
            mapError = { status ->
                when (status) {
                    HttpStatusCode.Conflict -> AppError.Auth.Server.UserAlreadyExists(login)
                    else -> null
                }
            }
        ).map { response ->
            tokenManager.saveTokens(
                response.user,
                response.accessToken,
                response.refreshToken
            )
            response.user
        }
    }

    override fun isAuthorized(): Flow<Boolean> = tokenManager.accessToken.map { it != null }

    override fun loginName(): Flow<String?> = tokenManager.login

    override suspend fun logout() {
        taskDao.clearAll()
        tokenManager.clear()
    }
}