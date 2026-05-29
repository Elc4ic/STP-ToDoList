package dev.stp.domain.repository

import dev.stp.domain.entity.UserEntity
import dev.stp.domain.entity.toUserEntity
import dev.stp.infrastructure.schema.UsersTable
import dev.stp.infrastructure.security.PasswordHasher
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import java.util.UUID

interface UserRepository {
    suspend fun findByLogin(login: String): UserEntity?
    suspend fun findById(id: String): UserEntity?
    suspend fun createUser(log: String, password: String): UserEntity
}

class PostgresUserRepository : UserRepository {

    override suspend fun findByLogin(login: String): UserEntity? = dbQuery {
        UsersTable.selectAll().where { UsersTable.login eq login }
            .map { it.toUserEntity() }
            .singleOrNull()
    }

    override suspend fun findById(id: String): UserEntity? = dbQuery {
        val uuid = UUID.fromString(id)
        UsersTable.selectAll().where { UsersTable.id eq uuid }
            .map { it.toUserEntity() }
            .singleOrNull()
    }

    override suspend fun createUser(log: String, password: String): UserEntity = dbQuery {
        val newId = UUID.randomUUID()
        val hash = PasswordHasher.hash(password)

        UsersTable.insert {
            it[id] = newId
            it[login] = log
            it[passwordHash] = hash
        }

        UserEntity(
            id = newId,
            login = log,
            passwordHash = hash
        )
    }
}