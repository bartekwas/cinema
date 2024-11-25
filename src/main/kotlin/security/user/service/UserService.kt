package com.bwasik.security.user.service

import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.bwasik.security.jwt.model.http.LoginRequest
import com.bwasik.security.jwt.model.http.LoginResponse
import com.bwasik.security.jwt.service.JWTService
import com.bwasik.security.user.model.User
import com.bwasik.security.user.respository.UserRepository
import com.bwasik.utils.toUUID
import org.slf4j.LoggerFactory

class UserService(
    private val userRepository: UserRepository,
    private val jwtService: JWTService
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun findAll(): List<User> =
        userRepository.findAll()

    fun findById(id: String): User? =
        userRepository.findById(
            id = id.toUUID()
        )

    fun insert(user: User): User? {
        val foundUser = userRepository.findById(user.id)
        if (foundUser != null) return null
        userRepository.insert(user)
        return user
    }

    fun authenticate(loginRequest: LoginRequest): LoginResponse? {
        return userRepository.findById(loginRequest.username.toUUID())?.let { user ->
            if (loginRequest.password != user.password) return null
            LoginResponse(jwtService.createAccessToken(user.username, user.role))
        }
    }

    private fun getDecodedJwt(token: String): DecodedJWT? =
        try {
            jwtService.jwtVerifier.verify(token)
        } catch (e: JWTVerificationException) {
            logger.error("Error while decoding JWT token", e)
            null
        }
}