package dev.stp.domain.entity

import dev.stp.infrastructure.schema.UsersTable
import dto.UserDto
import org.jetbrains.exposed.sql.ResultRow
import java.util.UUID

data class UserEntity(
    val id: UUID,
    val login: String,
    val passwordHash: String,
) {
    fun toDto(): UserDto =
        UserDto(
            id = this.id.toString(),
            login = this.login
        )
}

fun ResultRow.toUserEntity() = UserEntity(
    id = this[UsersTable.id],
    login = this[UsersTable.login],
    passwordHash = this[UsersTable.passwordHash],
)

