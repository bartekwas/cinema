package com.bwasik.security.user.model.http

import com.bwasik.utils.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class UserCreationResponse(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val username: String,
    val role: String,
)
