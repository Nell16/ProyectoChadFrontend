package com.example.proyectochadfrontend.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectochadfrontend.components.AppScaffold
import com.example.proyectochadfrontend.model.LoginResponse
import com.example.proyectochadfrontend.ui.theme.*

@Composable
fun MenuPrincipalScreen(
    user: LoginResponse,
    onLogout: () -> Unit,
    onNavigateToReparaciones: () -> Unit,
    onCrearReparacion: () -> Unit,
    onDiagnosticar: () -> Unit,
    onAsignarTecnico: () -> Unit,
    onSolicitudesDisponibles: () -> Unit,
    onGestionServicios: () -> Unit = {},
    onGestionComponentes: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onGestionUsuarios: () -> Unit = {},
) {
    AppScaffold(
        selectedItem = "home",
        onHomeClick = {},
        onProfileClick = onProfileClick,
        onSettingsClick = onSettingsClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "MENÚ PRINCIPAL",
                color = cyberpunkCyan,
                fontFamily = Rajdhani,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = cyberpunkDarkGray)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Bienvenido, ${user.primerNombre} ${user.primerApellido}",
                        color = cyberpunkYellow,
                        fontFamily = Rajdhani,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = user.correo,
                        color = cyberpunkPink,
                        fontFamily = Rajdhani,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Rol: ${user.rol.uppercase()}",
                        color = cyberpunkCyan,
                        fontFamily = Rajdhani,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val botones = when (user.rol.uppercase()) {
                "CLIENTE" -> listOf(
                    "Mis Reparaciones" to onNavigateToReparaciones,
                    "Solicitud - Nueva Reparación" to onCrearReparacion
                )
                "TECNICO" -> listOf(
                    "Reparaciones Asignadas" to onNavigateToReparaciones,
                    "Solicitudes Disponibles" to onSolicitudesDisponibles
                )
                "ADMIN" -> listOf(
                    "Gestionar Reparaciones" to onNavigateToReparaciones,
                    "Solicitudes Disponibles" to onAsignarTecnico,
                    "Gestionar Servicios" to onGestionServicios,
                    "Gestionar Componentes" to onGestionComponentes,
                    "Gestionar Usuarios" to onGestionUsuarios
                )
                else -> emptyList()
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(botones) { (texto, accion) ->
                    CyberGridButton(text = texto, onClick = accion)
                }
            }
        }
    }
}

@Composable
fun CyberGridButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = cyberpunkCyan,
            contentColor = Color.Black
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontFamily = Rajdhani,
            fontWeight = FontWeight.Bold
        )
    }
}