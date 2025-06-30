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
                                    navController.navigate("reparaciones")
                                },
                                onAsignarTecnico = {
                                    navController.navigate("solicitudesDisponibles")
                                },
                                onSolicitudesDisponibles = {
                                    navController.navigate("solicitudesDisponibles")
                                },
                                onGestionServicios = {
                                    navController.navigate("gestionServicios")
                                }

                            )
                        }
                    }

                    composable("reparaciones") {
                        usuarioLogueado?.let {
                            MisReparacionesScreen(
                                userId = it.idUsuario,
                                token = it.token,
                                rol = it.rol,
                                onBack = { navController.popBackStack() },
                                onVerDetalle = { reparacionId ->
                                    navController.navigate("detalleReparacionCliente/$reparacionId")
                                },
                                onAccionesReparacion = { reparacionId ->
                                    navController.navigate("accionesReparacion/$reparacionId")
                                }
                            )
                        }
                    }

                    composable("detalleReparacionCliente/{id}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id")?.toLongOrNull()
                        val token = usuarioLogueado?.token ?: ""
                        if (id != null) {
                            DetalleReparacionClienteScreen(
                                reparacionId = id,
                                token = token,
                                onVolver = { navController.popBackStack() }
                            )
                        }
                    }

                    composable("detalleReparacionTecnico/{id}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id")?.toLongOrNull()
                        val token = usuarioLogueado?.token ?: ""
                        if (id != null) {
                            DetalleReparacionTecnicoScreen(
                                reparacionId = id,
                                token = token,
                                onVolver = { navController.popBackStack() },
                                onTomarSolicitud = {
                                    navController.popBackStack()
                                }
                            )
                        } else {
                            // Manejo de error si el ID no es válido
                            // Puedes mostrar un Toast o log
                        }
                    }



                    composable("accionesReparacion/{reparacionId}") { backStackEntry ->
                        val reparacionId = backStackEntry.arguments?.getString("reparacionId")?.toLongOrNull()
                        usuarioLogueado?.let {
                            if (reparacionId != null) {
                                AccionesReparacionScreen(
                                    reparacionId = reparacionId,
                                    token = it.token,
                                    onBack = { navController.popBackStack() },
                                    onAsignarServicio = { id ->
                                        navController.navigate("asignarServicio/$id")
                                    },
                                    onCambiarEstado = { id ->
                                        navController.navigate("actualizarEstado/$id")
                                    }
                                )
                            }
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

                    //Asignar técnico con ID
                    composable("asignarTecnico/{reparacionId}") { backStackEntry ->
                        val reparacionId = backStackEntry.arguments?.getString("reparacionId")?.toLongOrNull()
                        usuarioLogueado?.let {
                            if (reparacionId != null) {
                                AsignarTecnicoScreen(
                                    token = it.token,
                                    reparacionId = reparacionId,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }

                    // Solicitudes disponibles con navegación a AsignarTecnicoScreen
                    composable("solicitudesDisponibles") {
                        SolicitudesDisponiblesScreen(
                            token = usuarioLogueado?.token ?: "",
                            rol = usuarioLogueado?.rol ?: "",
                            onVolver = { navController.popBackStack() },
                            onAsignarTecnico = { reparacionId ->
                                navController.navigate("asignarTecnico/$reparacionId")
                            },
                            onVerDetalle = { reparacionId ->
                                navController.navigate("detalleReparacionTecnico/$reparacionId")
                            }
                        )
                    }

                    composable("asignarServicio/{reparacionId}") { backStackEntry ->
                        val reparacionId = backStackEntry.arguments?.getString("reparacionId")?.toLongOrNull()
                        usuarioLogueado?.let {
                            if (reparacionId != null) {
                                AsignarServicioScreen(
                                    reparacionId = reparacionId,
                                    token = it.token,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }

                    composable("gestionServicios") {
                        usuarioLogueado?.let {
                            GestionServiciosScreen(
                                token = it.token,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }

                }
            }
        }
    }
}
