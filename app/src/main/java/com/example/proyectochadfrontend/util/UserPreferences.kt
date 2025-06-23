package com.example.proyectochadfrontend.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
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

    // Guardar los datos del usuario
    suspend fun saveUserData(token: String, rol: String, correo: String, idUsuario: Long) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[ROL_KEY] = rol
            preferences[CORREO_KEY] = correo
            preferences[ID_KEY] = idUsuario
        }
    }

    // Recuperar datos como Flow
    val token: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }
    val rol: Flow<String?> = context.dataStore.data.map { it[ROL_KEY] }
    val correo: Flow<String?> = context.dataStore.data.map { it[CORREO_KEY] }
    val idUsuario: Flow<Long?> = context.dataStore.data.map { it[ID_KEY] }

    // Recuperar el token de forma directa (ej. para Retrofit)
    suspend fun getToken(): String? {
        return context.dataStore.data.map { it[TOKEN_KEY] }.firstOrNull()
    }

    // Recuperar ID del usuario directamente
    suspend fun getUserId(): Long? {
        return context.dataStore.data.map { it[ID_KEY] }.firstOrNull()
    }

    // Limpiar los datos (logout)
    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
