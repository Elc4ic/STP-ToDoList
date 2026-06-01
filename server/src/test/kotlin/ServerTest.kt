package dev.stp

import apiRoutes.Api
import arrow.core.Either
import dto.AuthRequest
import dto.AuthResponse
import errors.IError
import errors.safeRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ServerTest {

    class AuthTest {
        @Test
        fun `test register success`() = testApplicationWithClient { client ->
            val response = client.post(Api.Auth.Register.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("testUser", "123456"))
            }

            assertEquals(HttpStatusCode.Created, response.status, response.body())
            val body = response.body<AuthResponse>()
            assertEquals("testUser", body.user.login)
        }

        @Test
        fun `test register with login only spaces`() = testApplicationWithClient { client ->
            val response = client.safeRequest<AuthResponse>(
                call = {
                    post(Api.Auth.Register.path) {
                        contentType(ContentType.Application.Json)
                        setBody(AuthRequest("   ", "123456"))
                    }
                },
                mapError = { statusCode ->
                    when (statusCode) {
                        HttpStatusCode.BadRequest -> IError.Auth.LoginFieldEmpty()
                        else -> null
                    }
                }
            )

            assertTrue(response.isLeft())
            val error = (response as Either.Left).value
            assertIs<IError.Auth.LoginFieldEmpty>(error)
        }

        @Test
        fun `test register with password only spaces`() = testApplicationWithClient { client ->
            val response = client.safeRequest<AuthResponse>(
                call = {
                    post(Api.Auth.Register.path) {
                        contentType(ContentType.Application.Json)
                        setBody(AuthRequest("testUser", "   "))
                    }
                },
                mapError = { statusCode ->
                    when (statusCode) {
                        HttpStatusCode.BadRequest -> IError.Auth.PasswordFieldEmpty()
                        else -> null
                    }
                }
            )

            assertTrue(response.isLeft())
            val error = (response as Either.Left).value
            assertIs<IError.Auth.PasswordFieldEmpty>(error)
        }

        @Test
        fun `test register with very long password`() = testApplicationWithClient { client ->
            val longPassword = "123456".repeat(20)
            val response = client.safeRequest<AuthResponse>(
                call = {
                    post(Api.Auth.Register.path) {
                        contentType(ContentType.Application.Json)
                        setBody(AuthRequest("userWithLongPassword", longPassword))
                    }
                },
                mapError = { statusCode ->
                    when (statusCode) {
                        HttpStatusCode.BadRequest -> IError.Auth.PasswordTooLong()
                        else -> null
                    }
                }
            )

            assertTrue(response.isLeft())
            val error = (response as Either.Left).value
            assertIs<IError.Auth.PasswordTooLong>(error)
        }

        @Test
        fun `test register with short password`() = testApplicationWithClient { client ->
            val response = client.safeRequest<AuthResponse>(
                call = {
                    post(Api.Auth.Register.path) {
                        contentType(ContentType.Application.Json)
                        setBody(AuthRequest("userWithShortPassword", "123"))
                    }
                },
                mapError = { statusCode ->
                    when (statusCode) {
                        HttpStatusCode.BadRequest -> IError.Auth.PasswordTooShort()
                        else -> null
                    }
                }
            )

            assertTrue(response.isLeft())
            val error = (response as Either.Left).value
            assertIs<IError.Auth.PasswordTooShort>(error)
        }

        @Test
        fun `test register with short login`() = testApplicationWithClient { client ->
            val response = client.safeRequest<AuthResponse>(
                call = {
                    post(Api.Auth.Register.path) {
                        contentType(ContentType.Application.Json)
                        setBody(AuthRequest("u", "123456"))
                    }
                },
                mapError = { statusCode ->
                    when (statusCode) {
                        HttpStatusCode.BadRequest -> IError.Auth.LoginTooShort()
                        else -> null
                    }
                }
            )

            assertTrue(response.isLeft())
            val error = (response as Either.Left).value
            assertIs<IError.Auth.LoginTooShort>(error)
        }

        @Test
        fun `test register with long login`() = testApplicationWithClient { client ->
            val longLogin = "123456".repeat(20)
            val response = client.safeRequest<AuthResponse>(
                call = {
                    post(Api.Auth.Register.path) {
                        contentType(ContentType.Application.Json)
                        setBody(AuthRequest(longLogin, "123456"))
                    }
                },
                mapError = { statusCode ->
                    when (statusCode) {
                        HttpStatusCode.BadRequest -> IError.Auth.LoginTooLong()
                        else -> null
                    }
                }
            )

            assertTrue(response.isLeft())
            val error = (response as Either.Left).value
            assertIs<IError.Auth.LoginTooLong>(error)
        }

        @Test
        fun `test register with special characters in login`() = testApplicationWithClient { client ->
            val response = client.safeRequest<AuthResponse>(
                call = {
                    post(Api.Auth.Register.path) {
                        contentType(ContentType.Application.Json)
                        setBody(AuthRequest("user@#$%^&*()", "123456"))
                    }
                },
                mapError = { statusCode ->
                    when (statusCode) {
                        HttpStatusCode.BadRequest -> IError.Auth.LoginHaveSpecSimbol()
                        else -> null
                    }
                }
            )

            assertTrue(response.isLeft())
            val error = (response as Either.Left).value
            assertIs<IError.Auth.LoginHaveSpecSimbol>(error)
        }

        @Test
        fun `test register with very long login`() = testApplicationWithClient { client ->
            val longLogin = "a".repeat(1000)
            val response = client.safeRequest<AuthResponse>(
                call = {
                    post(Api.Auth.Register.path) {
                        contentType(ContentType.Application.Json)
                        setBody(AuthRequest(longLogin, "123456"))
                    }
                },
                mapError = { statusCode ->
                    when (statusCode) {
                        HttpStatusCode.BadRequest -> IError.Auth.LoginTooLong()
                        else -> null
                    }
                }
            )

            assertTrue(response.isLeft())
            val error = (response as Either.Left).value
            assertIs<IError.Auth.LoginTooLong>(error)
        }

        @Test
        fun `test register empty password`() = testApplicationWithClient { client ->
            val response = client.safeRequest<AuthResponse>(
                call = {
                    post(Api.Auth.Register.path) {
                        contentType(ContentType.Application.Json)
                        setBody(AuthRequest("testUserEmpty", ""))
                    }
                },
                mapError = { statusCode ->
                    when (statusCode) {
                        HttpStatusCode.BadRequest -> IError.Auth.PasswordFieldEmpty()
                        else -> null
                    }
                }
            )

            assertTrue(response.isLeft())
            val error = (response as Either.Left).value
            assertIs<IError.Auth.PasswordFieldEmpty>(error)
        }

        @Test
        fun `test register empty login`() = testApplicationWithClient { client ->
            val response = client.safeRequest<AuthResponse>(
                call = {
                    post(Api.Auth.Register.path) {
                        contentType(ContentType.Application.Json)
                        setBody(AuthRequest("", "123456"))
                    }
                },
                mapError = { statusCode ->
                    when (statusCode) {
                        HttpStatusCode.BadRequest -> IError.Auth.LoginFieldEmpty()
                        else -> null
                    }
                }
            )

            assertTrue(response.isLeft())
            val error = (response as Either.Left).value
            assertIs<IError.Auth.LoginFieldEmpty>(error)
        }

        @Test
        fun `test register exist user`() = testApplicationWithClient { client ->
            client.safeRequest<AuthResponse>(
                call = {
                    post(Api.Auth.Register.path) {
                        contentType(ContentType.Application.Json)
                        setBody(AuthRequest("testUser", "123456"))
                    }
                }
            )

            val response = client.safeRequest<AuthResponse>(
                call = {
                    post(Api.Auth.Register.path) {
                        contentType(ContentType.Application.Json)
                        setBody(AuthRequest("testUser", "12345678"))
                    }
                },
                mapError = { statusCode ->
                    when (statusCode) {
                        HttpStatusCode.Conflict -> IError.Auth.UserAlreadyExists("testUser")
                        else -> null
                    }
                }
            )

            assertTrue(response.isLeft())
            val error = (response as Either.Left).value
            assertIs<IError.Auth.UserAlreadyExists>(error)
            assertEquals("testUser", (error as IError.Auth.UserAlreadyExists).login)
        }

        @Test
        fun `test register with sql injection`() = testApplicationWithClient { client ->
            val sqlInjectionLogin = "test'; DROP TABLE users; --"

            val response = client.post(Api.Auth.Register.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest(sqlInjectionLogin, "123456"))
            }

            val loginResponse = client.post(Api.Auth.Login.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("test'; DROP TABLE users; --", "123456"))
            }

            assertEquals(HttpStatusCode.Created, response.status, response.body())
        }

        @Test
        fun `test login success`() = testApplicationWithClient { client ->
            client.post(Api.Auth.Register.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("testUser", "123456"))
            }

            val response = client.post(Api.Auth.Login.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("testUser", "123456"))
            }

            assertEquals(HttpStatusCode.OK, response.status, response.body())
        }

        @Test
        fun `test login not-exist user`() = testApplicationWithClient { client ->
            val response = client.safeRequest<AuthResponse>(
                call = {
                    post(Api.Auth.Login.path) {
                        contentType(ContentType.Application.Json)
                        setBody(AuthRequest("testUser", "123456"))
                    }
                },
                mapError = { statusCode ->
                    when (statusCode) {
                        HttpStatusCode.Unauthorized -> IError.Auth.UserNotFound()
                        else -> null
                    }
                }
            )

            assertTrue(response.isLeft())
            val error = (response as Either.Left).value
            assertIs<IError.Auth.UserNotFound>(error)
        }

        @Test
        fun `test login exist user with wrong password`() = testApplicationWithClient { client ->
            client.post(Api.Auth.Register.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("testUser", "123456"))
            }

            val response = client.safeRequest<AuthResponse>(
                call = {
                    post(Api.Auth.Login.path) {
                        contentType(ContentType.Application.Json)
                        setBody(AuthRequest("testUser", "wrongpswd"))
                    }
                },
                mapError = { statusCode ->
                    when (statusCode) {
                        HttpStatusCode.Unauthorized -> IError.Auth.InvalidCredentials()
                        else -> null
                    }
                }
            )

            assertTrue(response.isLeft())
            val error = (response as Either.Left).value
            assertIs<IError.Auth.InvalidCredentials>(error)
        }

        @Test
        fun `test login with empty password`() = testApplicationWithClient { client ->
            client.post(Api.Auth.Register.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("testUser", "123456"))
            }

            val response = client.safeRequest<AuthResponse>(
                call = {
                    post(Api.Auth.Login.path) {
                        contentType(ContentType.Application.Json)
                        setBody(AuthRequest("testUser", ""))
                    }
                },
                mapError = { statusCode ->
                    when (statusCode) {
                        HttpStatusCode.BadRequest -> IError.Auth.PasswordFieldEmpty()
                        else -> null
                    }
                }
            )

            assertTrue(response.isLeft())
            val error = (response as Either.Left).value
            assertIs<IError.Auth.PasswordFieldEmpty>(error)
        }

        @Test
        fun `test login with empty login`() = testApplicationWithClient { client ->
            client.post(Api.Auth.Register.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("testUser", "123456"))
            }

            val response = client.safeRequest<AuthResponse>(
                call = {
                    post(Api.Auth.Login.path) {
                        contentType(ContentType.Application.Json)
                        setBody(AuthRequest("", "123456"))
                    }
                },
                mapError = { statusCode ->
                    when (statusCode) {
                        HttpStatusCode.BadRequest -> IError.Auth.LoginFieldEmpty()
                        else -> null
                    }
                }
            )

            assertTrue(response.isLeft())
            val error = (response as Either.Left).value
            assertIs<IError.Auth.LoginFieldEmpty>(error)
        }

        @Test
        fun `test login with whitespace in login`() = testApplicationWithClient { client ->
            client.post(Api.Auth.Register.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("testUser", "123456"))
            }

            val response = client.post(Api.Auth.Login.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("  testUser  ", "123456"))
            }

            assertEquals(HttpStatusCode.OK, response.status, response.body())
        }

        @Test
        fun `test login with case-sensitive login`() = testApplicationWithClient { client ->
            client.post(Api.Auth.Register.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("TestUser", "123456"))
            }

            val response = client.safeRequest<AuthResponse>(
                call = {
                    post(Api.Auth.Login.path) {
                        contentType(ContentType.Application.Json)
                        setBody(AuthRequest("testuser", "123456"))
                    }
                },
                mapError = { statusCode ->
                    when (statusCode) {
                        HttpStatusCode.Unauthorized -> IError.Auth.InvalidCredentials()
                        else -> null
                    }
                }
            )

            assertTrue(response.isLeft())
            val error = (response as Either.Left).value
            assertIs<IError.Auth.InvalidCredentials>(error)
        }

        @Test
        fun `test login with sql injection`() = testApplicationWithClient { client ->
            val normalUser = "normal_user"
            val password = "123456"

            client.post(Api.Auth.Register.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest(normalUser, password))
            }

            val sqlInjectionLogin = "' OR '1'='1"

            val response = client.safeRequest<AuthResponse>(
                call = {
                    post(Api.Auth.Login.path) {
                        contentType(ContentType.Application.Json)
                        setBody(AuthRequest(sqlInjectionLogin, password))
                    }
                },
                mapError = { statusCode ->
                    when (statusCode) {
                        HttpStatusCode.Unauthorized -> IError.Auth.InvalidCredentials()
                        else -> null
                    }
                }
            )

            assertTrue(response.isLeft())
            val error = (response as Either.Left).value
            assertIs<IError.Auth.InvalidCredentials>(error)
        }

        companion object {
            fun testApplicationWithClient(block: suspend ApplicationTestBuilder.(HttpClient) -> Unit) =
                testApplication {
                    val testDbUrl = "jdbc:h2:mem:test_db_${java.util.UUID.randomUUID()};DB_CLOSE_DELAY=-1"

                    environment {
                        config = MapApplicationConfig(
                            "database.url" to testDbUrl,
                            "database.driver" to "org.h2.Driver"
                        )
                    }

                    application {
                        module()
                    }

                    val client = createClient {
                        install(ContentNegotiation) { json() }
                    }

                    block(client)
                }
        }
    }
}