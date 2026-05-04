package dev.stp.app.data.repository

import apiRoutes.Api
import dev.stp.app.data.datasource.TokenManager
import dev.stp.app.domain.repository.AuthRepository
import dto.AuthRequest
import dto.AuthResponse
import dto.UserDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode

class AuthRepositoryImpl(
    private val client: HttpClient,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(login: String, password: String): Result<UserDto> {
        return try {
            val response = client.post(Api.Auth.Login.path) {
                setBody(AuthRequest(login, password))
            }

            if (response.status == HttpStatusCode.OK) {
                val authResponse = response.body<AuthResponse>()
                tokenManager.saveTokens(
                    authResponse.accessToken,
                    authResponse.refreshToken
                )
                Result.success(authResponse.user)
            } else {
                Result.failure(Exception("Ошибка авторизации: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(login: String, password: String): Result<UserDto> {
        return try {
            val response = client.post(Api.Auth.Register.path) {
                setBody(AuthRequest(login, password))
            }

            if (response.status == HttpStatusCode.OK) {
                val authResponse = response.body<AuthResponse>()
                tokenManager.saveTokens(
                    authResponse.accessToken,
                    authResponse.refreshToken
                )
                Result.success(authResponse.user)
            } else {
                Result.failure(Exception("Ошибка регистрации: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        tokenManager.clear()
    }
}