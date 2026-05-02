package dev.stp.app.data.datasource

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TokenManager(private val context: Context) {
    private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

    private val accessTokenKey = stringPreferencesKey("access_token")
    private val refreshTokenKey = stringPreferencesKey("refresh_token")

    val accessToken: Flow<String?> = context.dataStore.data.map { it[accessTokenKey] }
    val refreshToken: Flow<String?> = context.dataStore.data.map { it[refreshTokenKey] }

    suspend fun saveTokens(access: String, refresh: String) {
        context.dataStore.edit {
            it[accessTokenKey] = access
            it[refreshTokenKey] = refresh
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}