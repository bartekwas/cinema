package com.bwasik.security.user.model.http

import kotlinx.serialization.Serializable

@Serializable
data class UserCreationRequest(
    val username: String,
    val password: String,
)
