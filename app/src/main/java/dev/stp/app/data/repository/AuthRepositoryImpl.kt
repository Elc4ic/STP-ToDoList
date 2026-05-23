package dev.stp.app.data.repository

import apiRoutes.Api
import arrow.core.Either
import arrow.core.Option
import arrow.core.raise.either
import dev.stp.app.data.datasource.TokenStore
import dev.stp.app.data.localDB.TaskDao
import dev.stp.app.data.mapper.safeApiCall
import dev.stp.app.domain.repository.AuthRepository
import dto.AuthRequest
import dto.AuthResponse
import dto.UserDto
import errors.AppError
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val client: HttpClient,
    private val taskDao: TaskDao,
    private val tokenStore: TokenStore
) : AuthRepository {

    override suspend fun login(login: String, password: String): Either<AppError, UserDto> =
        either {
            val response = safeApiCall<AuthResponse>(
                call = {
                    client.post(Api.Auth.Login.url()) {
                        setBody(AuthRequest(login, password))
                    }
                },
                mapError = { status ->
                    if (status == HttpStatusCode.NotFound) AppError.Server.Auth.UserNotFound() else null
                }
            ).bind()

            tokenStore.saveTokens(
                response.user,
                response.accessToken,
                response.refreshToken
            )

            response.user
        }

    override suspend fun register(login: String, password: String): Either<AppError, UserDto> =
        either {
            val response = safeApiCall<AuthResponse>(
                call = {
                    client.post(Api.Auth.Register.url()) {
                        setBody(AuthRequest(login, password))
                    }
                },
                mapError = { status ->
                    if (status == HttpStatusCode.NotFound) AppError.Server.Auth.UserNotFound() else null
                }
            ).bind()

            tokenStore.saveTokens(
                response.user,
                response.accessToken,
                response.refreshToken
            )

            response.user
        }

    override fun isAuthorized(): Flow<Boolean> =
        tokenStore.accessToken.map { option -> option.isSome() }

    override fun loginName(): Flow<Option<String>> = tokenStore.login

    override suspend fun logout() {
        taskDao.clearAll()
        tokenStore.clear()
    }
}