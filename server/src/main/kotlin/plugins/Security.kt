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
                val userId = credential.payload.getClaim("userId").asString()
                val tokenType = credential.payload.getClaim("type").asString()

                if (!userId.isNullOrEmpty() && tokenType == "access") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}