package com.bwasik.security.user.respository

import com.bwasik.security.user.model.User
import org.koin.core.component.KoinComponent
import java.util.*

class UserRepository {
    private val users = mutableListOf<User>()

    fun findAll(): List<User> =
        users

    fun findById(id: UUID): User? =
        users.firstOrNull { it.id == id }

    fun insert(user: User): Boolean =
        users.add(user)
}