package com.bwasik.security.user.service

import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.bwasik.security.jwt.model.http.LoginRequest
import com.bwasik.security.jwt.model.http.LoginResponse
import com.bwasik.security.jwt.service.JWTService
import com.bwasik.security.user.model.User
import com.bwasik.security.user.model.UserRole
import com.bwasik.security.user.model.http.UserCreationRequest
import com.bwasik.security.user.model.http.UserCreationResponse
import com.bwasik.security.user.respository.UserRepository
import com.bwasik.utils.safeValueOf
import com.bwasik.utils.toUUID
import org.mindrot.jbcrypt.BCrypt
import org.slf4j.LoggerFactory
import java.util.*

class UserService(
    private val userRepository: UserRepository,
    private val jwtService: JWTService
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun findAll(): List<UserCreationResponse> =
        userRepository.findAll().map { it.toUserCreationResponse() }

    fun findById(id: String): User? =
        userRepository.findById(
            id = id.toUUID()
        )

    fun insert(userRequest: UserCreationRequest): User? {
        val user = userRequest.toUserModel()
        val foundUser = userRepository.findById(user.id)
        if (foundUser != null) return null
        userRepository.insert(user)
        return user
    }

    fun authenticate(loginRequest: LoginRequest): LoginResponse? {
        return userRepository.findById(loginRequest.username.toUUID())?.let { user ->
            if(!verifyPassword(loginRequest.password, user.password)) return null
            LoginResponse(jwtService.createAccessToken(user.username, user.role, user.id))
        }
    }
}

private fun UserCreationRequest.toUserModel(): User  =
    User(
        id = UUID.nameUUIDFromBytes(this.username.toByteArray()),
        username = this.username,
        password = hashPassword(this.password),
        role = this.role.safeValueOf<UserRole>() ?: UserRole.USER
    )

private fun User.toUserCreationResponse(): UserCreationResponse =
    UserCreationResponse(
        id = this.id,
        username = this.username,
        role = this.role.name
    )

fun hashPassword(plainPassword: String): String {
    return BCrypt.hashpw(plainPassword, BCrypt.gensalt())
}

fun verifyPassword(plainPassword: String, hashedPassword: String): Boolean {
    return BCrypt.checkpw(plainPassword, hashedPassword)
}