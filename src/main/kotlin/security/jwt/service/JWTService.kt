package com.bwasik.security.jwt.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.bwasik.security.jwt.JWTConfig
import io.ktor.server.auth.jwt.*
import org.slf4j.LoggerFactory
import java.util.*

const val TOKEN_EXPIRATION_TIME = 900_000
const val USERNAME_CLAIM = "username"

class JWTService(
    private val jwtConfig: JWTConfig,
) {
    val logger = LoggerFactory.getLogger(this::class.java)
    val realm = jwtConfig.realm

    val jwtVerifier: JWTVerifier =
        JWT.require(Algorithm.HMAC256(jwtConfig.secret))
            .withAudience(jwtConfig.audience)
            .withIssuer(jwtConfig.issuer)
            .build()

    fun createAccessToken(username: String): String =
        JWT.create()
            .withAudience(jwtConfig.audience)
            .withIssuer(jwtConfig.issuer)
            .withClaim(USERNAME_CLAIM, username)
            .withExpiresAt(Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME))
            .sign(Algorithm.HMAC256(jwtConfig.secret))


    fun customValidator(
        credential: JWTCredential
    ): JWTPrincipal? =
        credential.takeIf { it.audienceMatches() }?.let {
            JWTPrincipal(credential.payload)
        }

    private fun getDecodedJwt(token: String): DecodedJWT? =
        try {
            jwtVerifier.verify(token)
        } catch (e: JWTVerificationException) {
            logger.error("Error while decoding JWT token", e)
            null
        }


    private fun JWTCredential.audienceMatches(): Boolean =
        this.payload.audience.contains(jwtConfig.audience)
}

fun JWTCredential.extractUsername(): String? =
    this.payload.getClaim(USERNAME_CLAIM).asString()