package dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(val token: String, val user: UserDto)