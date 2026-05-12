package dev.stp

import apiRoutes.Api
import dev.stp.plugins.configureRouting
import dev.stp.plugins.configureSecurity
import dto.AuthRequest
import dto.AuthResponse
import dto.UserDto
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.server.application.Application
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ServerTest {

    class AuthTest {
        @Test
        fun `test register success`() = testApplication {
            application {
                module()
            }

            val client = createClient {
                this.install(ContentNegotiation) {
                    json()
                }
            }

            val response = client.post(Api.Auth.Register.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("testUser", "123456"))
            }

            assertEquals(HttpStatusCode.Created, response.status)
            val body = response.body<AuthResponse>()
            assertEquals("testUser", body.user.login)
        }

        @Test
        fun `test register empty password`() = testApplication {
            application {
                module()
            }

            val client = createClient {
                this.install(ContentNegotiation) {
                    json()
                }
            }

            val response = client.post(Api.Auth.Register.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("testUserEmpty", ""))
            }

            assertEquals(HttpStatusCode.Conflict, response.status)
        }

        @Test
        fun `test register empty login`() = testApplication {
            application {
                module()
            }

            val client = createClient {
                this.install(ContentNegotiation) {
                    json()
                }
            }

            val response = client.post(Api.Auth.Register.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("", "12345"))
            }

            assertEquals(HttpStatusCode.Conflict, response.status)
        }

        @Test
        fun `test register empty login and password`() = testApplication {
            application {
                module()
            }

            val client = createClient {
                this.install(ContentNegotiation) {
                    json()
                }
            }

            val response = client.post(Api.Auth.Register.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("", ""))
            }

            assertEquals(HttpStatusCode.Conflict, response.status)
        }

        @Test
        fun `test register exist user`() = testApplication {
            application {
                module()
            }

            val client = createClient {
                this.install(ContentNegotiation) {
                    json()
                }
            }

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
        fun `test login success`() = testApplication {
            application {
                module()
            }

            val client = createClient {
                this.install(ContentNegotiation) {
                    json()
                }
            }

            val response = client.post(Api.Auth.Login.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("testUser", "123456"))
            }

            assertEquals(HttpStatusCode.OK, response.status)
        }

        @Test
        fun `test login not-exist user`() = testApplication {
            application {
                module()
            }

            val client = createClient {
                this.install(ContentNegotiation) {
                    json()
                }
            }

            val response = client.post(Api.Auth.Login.path) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest("test", "123456"))
            }

            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
    }
}
