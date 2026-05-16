package dev.stp.app.data.datasource

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dto.UserDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class TokenManager(private val context: Context) {
    private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

    private val userIdKey = stringPreferencesKey("userId")
    private val loginKey = stringPreferencesKey("login")
    private val accessTokenKey = stringPreferencesKey("access_token")
    private val refreshTokenKey = stringPreferencesKey("refresh_token")

    val userId: Flow<UUID?> = context.dataStore.data
        .map { it[userIdKey] }
        .map { UUID.fromString(it) }

    val login: Flow<String?> = context.dataStore.data.map { it[loginKey] }
    val accessToken: Flow<String?> = context.dataStore.data.map { it[accessTokenKey] }
    val refreshToken: Flow<String?> = context.dataStore.data.map { it[refreshTokenKey] }

    suspend fun saveTokens(user: UserDto, access: String, refresh: String) {
        context.dataStore.edit {
            it[loginKey] = user.login
            it[userIdKey] = user.id
            it[accessTokenKey] = access
            it[refreshTokenKey] = refresh
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}