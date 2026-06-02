package dev.stp.app.data.datasource

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import arrow.core.Either
import arrow.core.Option
import dto.UserDto
import errors.IError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

interface TokenStore {
    val userId: Flow<Option<UUID>>
    val login: Flow<Option<String>>
    val accessToken: Flow<Option<String>>
    val refreshToken: Flow<Option<String>>

    suspend fun saveTokens(
        user: UserDto,
        access: String,
        refresh: String
    ): Either<IError, Preferences>

    suspend fun clear()
}

class DataStoreTokenStore(private val context: Context) : TokenStore {
    private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

    private val userIdKey = stringPreferencesKey("userId")
    private val loginKey = stringPreferencesKey("login")
    private val accessTokenKey = stringPreferencesKey("access_token")
    private val refreshTokenKey = stringPreferencesKey("refresh_token")

    private fun <T> read(key: Preferences.Key<T>): Flow<Option<T>> =
        context.dataStore.data.map { Option.fromNullable(it[key]) }

    override val login = read(loginKey)
    override val accessToken = read(accessTokenKey)
    override val refreshToken = read(refreshTokenKey)

    override val userId: Flow<Option<UUID>> = read(userIdKey).map { option ->
        option.flatMap { str ->
            Option.catch { UUID.fromString(str) }
        }
    }

    override suspend fun saveTokens(user: UserDto, access: String, refresh: String) = Either.catch {
        context.dataStore.edit {
            it[loginKey] = user.login
            it[userIdKey] = user.id
            it[accessTokenKey] = access
            it[refreshTokenKey] = refresh
        }
    }.mapLeft { IError.Auth.CannotSaveToken() }

    override suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}