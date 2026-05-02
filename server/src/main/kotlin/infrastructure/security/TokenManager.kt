package dev.stp.infrastructure.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

class TokenManager {
    private val secret = "my-super-secret-key"
    private val issuer = "dev.stp"
    private val audience = "android-client"
    private val algorithm = Algorithm.HMAC256(secret)

    private val ACCESS_TOKEN_EXPIRATION = 60 * 60 * 1000
    private val REFRESH_TOKEN_EXPIRATION = 30L * 24 * 60 * 60 * 1000

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withAudience(audience)
        .withIssuer(issuer)
        .build()

    fun generateAccessToken(userId: String, login: String): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("userId", userId)
            .withClaim("login", login)
            .withClaim("type", "access")
            .withExpiresAt(Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
            .sign(algorithm)
    }

    fun generateRefreshToken(userId: String): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("userId", userId)
            .withClaim("type", "refresh")
            .withExpiresAt(Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
            .sign(algorithm)
    }

    fun verifyRefreshToken(token: String): String? {
        return try {
            val decoded = verifier.verify(token)
            if (decoded.getClaim("type").asString() != "refresh") return null

            decoded.getClaim("userId").asString()
        } catch (e: Exception) {
            null
        }
    }
}