package dev.stp.domain.repository

import dev.stp.domain.entity.UserEntity
import dev.stp.domain.entity.toUserEntity
import dev.stp.infrastructure.schema.UsersTable
import dev.stp.infrastructure.security.PasswordHasher
import dto.AuthRequest
import dto.UserDto
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

interface UserRepository {
    suspend fun findUserByLogin(login: String): UserEntity?
    suspend fun createUser(request: AuthRequest): UserDto
}

class PostgresUserRepository : UserRepository {

    override suspend fun findUserByLogin(login: String): UserEntity? = dbQuery {
        UsersTable.selectAll().where { UsersTable.login eq login }
            .map { it.toUserEntity() }
            .singleOrNull()
    }

    override suspend fun createUser(request: AuthRequest): UserDto = dbQuery {
        val newId = UUID.randomUUID()

        UsersTable.insert {
            it[id] = newId
            it[login] = request.login
            it[passwordHash] = PasswordHasher.hash(request.password)
        }

        UserDto(
            id = newId.toString(),
            login = request.login
        )
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}