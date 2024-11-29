package com.bwasik.security.user.respository

import com.bwasik.security.user.model.User
import com.bwasik.security.user.model.db.Users
import com.bwasik.security.user.model.db.Users.password
import com.bwasik.security.user.model.db.Users.role
import com.bwasik.security.user.model.db.Users.username
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import java.util.*

class UserRepository {

    fun findAll(): List<User> = transaction {
        Users.selectAll().map { rowToUser(it) }
    }

    fun findById(id: UUID): User? = transaction {
        Users.select { Users.id eq id }
            .mapNotNull { rowToUser(it) }
            .singleOrNull()
    }

    fun insert(user: User): Unit = transaction {
        Users.insert {
            it[id] = user.id
            it[username] = user.username
            it[password] = user.password
            it[role] = user.role
        }.insertedCount > 0
    }

    private fun rowToUser(row: ResultRow): User {
        return User(
            id = row[Users.id],
            username = row[Users.username],
            password = row[Users.password],
            role = row[Users.role]
        )
    }
}