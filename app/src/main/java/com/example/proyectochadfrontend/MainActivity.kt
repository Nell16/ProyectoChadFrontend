package com.example.proyectochadfrontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.proyectochadfrontend.data.LoginResponse
import com.example.proyectochadfrontend.screen.LoginScreen
import com.example.proyectochadfrontend.screen.MenuPrincipalScreen
import com.example.proyectochadfrontend.screen.MisReparacionesScreen
import com.example.proyectochadfrontend.util.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var usuarioLogueado by remember { mutableStateOf<LoginResponse?>(null) }
            var pantallaActual by remember { mutableStateOf("menu") }

            val context = LocalContext.current
            val userPrefs = remember { UserPreferences(context) }

            when {
                usuarioLogueado == null -> {
                    LoginScreen { loginResponse ->
                        // Guardamos en DataStore
                        CoroutineScope(Dispatchers.IO).launch {
                            userPrefs.saveUserData(
                                token = loginResponse.token,
                                rol = loginResponse.rol,
                                correo = loginResponse.correo,
                                idUsuario = loginResponse.idUsuario
                            )
                        }
                        usuarioLogueado = loginResponse
                    }
                }

                pantallaActual == "reparaciones" -> {
                    MisReparacionesScreen(
                        userId = usuarioLogueado!!.idUsuario,
                        token = usuarioLogueado!!.token
                    )
                }

                else -> {
                    MenuPrincipalScreen(
                        user = usuarioLogueado!!,
                        onLogout = {
                            CoroutineScope(Dispatchers.IO).launch {
                                userPrefs.clear()
                            }
                            usuarioLogueado = null
                        },
                        onNavigateToReparaciones = {
                            pantallaActual = "reparaciones"
                        }
                    )
                }
            }
        }
    }
}
