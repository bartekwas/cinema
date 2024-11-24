package com.bwasik.user.service

import com.bwasik.user.model.User
import com.bwasik.user.respository.UserRepository
import java.util.*

class UserService(
    private val userRepository: UserRepository
) {
    fun findAll(): List<User> =
        userRepository.findAll()

    fun findById(id: String): User? =
        userRepository.findById(
            id = UUID.fromString(id)
        )

    fun findByUsername(username: String): User? =
        userRepository.findByUsername(username)

    fun insert(user: User): User? {
        val foundUser = userRepository.findByUsername(user.username)

        return if (foundUser == null) {
            userRepository.insert(user)
            user
        } else null
    }
}