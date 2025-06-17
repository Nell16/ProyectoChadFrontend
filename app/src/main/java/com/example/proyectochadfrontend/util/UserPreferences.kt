package com.example.proyectochadfrontend.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension para acceder fácilmente
val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val TOKEN_KEY = stringPreferencesKey("token")
        val ROL_KEY = stringPreferencesKey("rol")
        val CORREO_KEY = stringPreferencesKey("correo")
        val ID_KEY = longPreferencesKey("idUsuario")
    }

    suspend fun saveUserData(token: String, rol: String, correo: String, idUsuario: Long) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[ROL_KEY] = rol
            preferences[CORREO_KEY] = correo
            preferences[ID_KEY] = idUsuario
        }
    }

    val token: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }
    val rol: Flow<String?> = context.dataStore.data.map { it[ROL_KEY] }
    val correo: Flow<String?> = context.dataStore.data.map { it[CORREO_KEY] }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}