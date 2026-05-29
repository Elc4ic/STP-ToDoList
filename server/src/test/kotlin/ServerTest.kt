package dev.stp

import apiRoutes.Api
import dto.AuthRequest
import dto.AuthResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ServerTest {

    class AuthTest {
        @Test
        fun `test register success`() = testApplicationWithClient { client ->
            val response = client.post(Api.Auth.Register.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("testUser", "123456"))
            }

            assertEquals(HttpStatusCode.Created, response.status)
            val body = response.body<AuthResponse>()
            assertEquals("testUser", body.user.login)
        }

        @Test
        fun `test register with login only spaces`() = testApplicationWithClient { client ->

            val response = client.post(Api.Auth.Register.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("   ", "123456"))
            }

            assertEquals(HttpStatusCode.Conflict, response.status)
        }

        @Test
        fun `test register with password only spaces`() = testApplicationWithClient { client ->

            val response = client.post(Api.Auth.Register.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("testUser", "   "))
            }

            assertEquals(HttpStatusCode.Conflict, response.status)
        }

        @Test
        fun `test register with very long password`() = testApplicationWithClient { client ->

            val longPassword = "123456".repeat(20)
            val response = client.post(Api.Auth.Register.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("userWithLongPassword", longPassword))
            }

            assertEquals(HttpStatusCode.InternalServerError, response.status)
        }

        @Test
        fun `test register with short password`() = testApplicationWithClient { client ->

            val response = client.post(Api.Auth.Register.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("userWithShortPassword", "123"))
            }

            assertEquals(HttpStatusCode.Conflict, response.status)
        }

        @Test
        fun `test register with special characters in login`() =
            testApplicationWithClient { client ->

                val response = client.post(Api.Auth.Register.path) {
                    contentType(ContentType.Application.Json)
                    setBody(AuthRequest("user@#$%^&*()", "123456"))
                }

                assertEquals(HttpStatusCode.Conflict, response.status)
            }

        @Test
        fun `test register with very long login`() = testApplicationWithClient { client ->

            val longLogin = "a".repeat(1000)

            val response = client.post(Api.Auth.Register.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest(longLogin, "123456"))
            }

            assertEquals(HttpStatusCode.InternalServerError, response.status)
        }

        @Test
        fun `test register empty password`() = testApplicationWithClient { client ->

            val response = client.post(Api.Auth.Register.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("testUserEmpty", ""))
            }

            assertEquals(HttpStatusCode.Conflict, response.status)
        }

        @Test
        fun `test register empty login`() = testApplicationWithClient { client ->

            val response = client.post(Api.Auth.Register.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("", "123456"))
            }

            assertEquals(HttpStatusCode.Conflict, response.status)
        }

        @Test
        fun `test register exist user`() = testApplicationWithClient { client ->

            client.post(Api.Auth.Register.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("testUser", "123456"))
            }

            val response = client.post(Api.Auth.Register.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("testUser", "12345678"))
            }

            assertEquals(HttpStatusCode.Conflict, response.status)
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

            assertEquals(HttpStatusCode.OK, loginResponse.status)
        }

        @Test
        fun `test login success`() = testApplicationWithClient { client ->

            val response = client.post(Api.Auth.Login.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("testUser", "123456"))
            }

            assertEquals(HttpStatusCode.OK, response.status)
        }

        @Test
        fun `test login not-exist user`() = testApplicationWithClient { client ->

            val response = client.post(Api.Auth.Login.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("test", "123456"))
            }

            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }

        @Test
        fun `test login exist user with wrong password`() = testApplicationWithClient { client ->

            val response = client.post(Api.Auth.Login.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("testUser", "wrongpswd"))
            }

            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }

        @Test
        fun `test login with empty password`() = testApplicationWithClient { client ->

            val response = client.post(Api.Auth.Login.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("testUser", ""))
            }

            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }

        @Test
        fun `test login with empty login`() = testApplicationWithClient { client ->

            val response = client.post(Api.Auth.Login.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("", "123456"))
            }

            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }


        // на ваше усмотрение, если будете обрезать пробелы, то поменяйте статус на OK
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

            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }

        @Test
        fun `test login with case-sensitive login`() = testApplicationWithClient { client ->

            client.post(Api.Auth.Register.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("TestUser", "123456"))
            }

            val response = client.post(Api.Auth.Login.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("testuser", "123456"))
            }

            assertEquals(HttpStatusCode.Unauthorized, response.status)
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

            val response = client.post(Api.Auth.Login.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest(sqlInjectionLogin, password))
            }

            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }

        companion object {
            fun testApplicationWithClient(block: suspend ApplicationTestBuilder.(HttpClient) -> Unit) =
                testApplication {
                    application {
                        module()
                    }

                    val client = createClient {
                        install(ContentNegotiation) {
                            json()
                        }
                    }

                    block(client)
                }
        }
    }
}
