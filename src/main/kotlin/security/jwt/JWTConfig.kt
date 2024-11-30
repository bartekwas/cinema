package com.bwasik.security.jwt

data class JWTConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String,
)
