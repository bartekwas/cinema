package com.bwasik.security.user.model.db

import com.bwasik.security.user.model.UserRole
import org.jetbrains.exposed.sql.Table

object Users : Table("users") {
    val id = uuid("id").uniqueIndex()
    val username = varchar("username", 50).uniqueIndex()
    val password = varchar("password", 255)
    val role = enumerationByName("role", 10, UserRole::class)

    override val primaryKey = PrimaryKey(id, name = "PK_Users_Id") // Primary key on id
}
