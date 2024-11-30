package com.bwasik.security.jwt.model.http

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val accessToken: String,
)
