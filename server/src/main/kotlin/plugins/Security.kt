package dev.stp.plugins

import dev.stp.infrastructure.security.TokenManager
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import org.koin.ktor.ext.inject

fun Application.configureSecurity(jwtName: String) {
    val tokenManager: TokenManager by inject()

    install(Authentication) {
        jwt(jwtName) {
            verifier(tokenManager.verifier)
            validate { credential ->
                if (credential.payload.getClaim("login").asString().isNotEmpty()) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
}