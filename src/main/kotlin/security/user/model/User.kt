package com.bwasik.security.user.model

import java.util.*

data class User(
    val id: UUID,
    val username: String,
    val password: String,
    val role: UserRole
)

enum class UserRole{
    USER, ADMIN
}