package dev.stp.app.data.repository

import apiRoutes.Api
import arrow.core.Either
import arrow.core.Option
import arrow.core.raise.either
import dev.stp.app.data.datasource.TokenStore
import dev.stp.app.data.localDB.TaskDao
import dev.stp.app.domain.repository.AuthRepository
import dto.AuthRequest
import dto.AuthResponse
import dto.UserDto
import errors.IError
import errors.safeRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val client: HttpClient,
    private val taskDao: TaskDao,
    private val tokenStore: TokenStore
) : AuthRepository {

    override suspend fun login(login: String, password: String): Either<IError, UserDto> =
        either {
            val response = client.safeRequest<AuthResponse>(
                call = {
                    post(Api.Auth.Login.url()) {
                        setBody(AuthRequest(login, password))
                    }
                }
            ).bind()

            tokenStore.saveTokens(
                response.user,
                response.accessToken,
                response.refreshToken
            )

            response.user
        }

    override suspend fun register(login: String, password: String): Either<IError, UserDto> =
        either {
            val response = client.safeRequest<AuthResponse>(
                call = {
                    post(Api.Auth.Register.url()) {
                        setBody(AuthRequest(login, password))
                    }
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