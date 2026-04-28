package dev.stp.infrastructure.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

class TokenManager {
    private val secret: String = "my-secret-key"
    private val issuer: String = "dev.stp"
    private val audience = "android-client"
    private val algorithm = Algorithm.HMAC256(secret)
    private val expiredTime = 1000 * 60 * 60 * 24

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withAudience(audience)
        .withIssuer(issuer)
        .build()

    fun generateToken(login: String): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("login", login)
            .withExpiresAt(Date(System.currentTimeMillis() + expiredTime))
            .sign(algorithm)
    }
}