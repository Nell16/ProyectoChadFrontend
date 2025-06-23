package com.example.proyectochadfrontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.*
import com.example.proyectochadfrontend.data.LoginResponse
import com.example.proyectochadfrontend.screen.*
import com.example.proyectochadfrontend.ui.theme.ProyectoChadFrontendTheme
import com.example.proyectochadfrontend.util.UserPreferences
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ProyectoChadFrontendTheme {
                val navController = rememberNavController()
                var usuarioLogueado by remember { mutableStateOf<LoginResponse?>(null) }
                val context = LocalContext.current
                val userPrefs = remember { UserPreferences(context) }
                val scope = rememberCoroutineScope()

                NavHost(navController = navController, startDestination = "login") {

                    composable("login") {
                        LoginScreen { loginResponse ->
                            usuarioLogueado = loginResponse
                            scope.launch {
                                userPrefs.saveUserData(
                                    token = loginResponse.token,
                                    rol = loginResponse.rol,
                                    correo = loginResponse.correo,
                                    idUsuario = loginResponse.idUsuario
                                )
                            }
                            navController.navigate("menu") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }

                    composable("menu") {
                        usuarioLogueado?.let { user ->
                            MenuPrincipalScreen(
                                user = user,
                                onLogout = {
                                    scope.launch { userPrefs.clear() }
                                    usuarioLogueado = null
                                    navController.navigate("login") {
                                        popUpTo("menu") { inclusive = true }
                                    }
                                },
                                onNavigateToReparaciones = {
                                    navController.navigate("reparaciones")
                                },
                                onCrearReparacion = {
                                    navController.navigate("crearReparacion")
                                },
                                onDiagnosticar = {
                                    navController.navigate("reparaciones") // puedes ajustar si hay pantalla específica
                                },
                                onAsignarTecnico = {
                                    navController.navigate("asignarTecnico")
                                },
                                onSolicitudesDisponibles = {
                                    navController.navigate("solicitudesDisponibles")
                                }
                            )
                        }
                    }

                    composable("reparaciones") {
                        usuarioLogueado?.let {
                            MisReparacionesScreen(
                                userId = it.idUsuario,
                                token = it.token,
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }

                    composable("crearReparacion") {
                        usuarioLogueado?.let {
                            CrearReparacionScreen(
                                userId = it.idUsuario,
                                token = it.token,
                                onReparacionCreada = {
                                    navController.popBackStack()
                                },
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }

                    composable("asignarTecnico") {
                        usuarioLogueado?.let {
                            AsignarTecnicoScreen(
                                token = it.token,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }

                    composable("solicitudesDisponibles") {
                        usuarioLogueado?.let {
                            SolicitudesDisponiblesScreen(
                                token = it.token,
                                onVolver = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
