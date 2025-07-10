package com.example.proyectochadfrontend.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectochadfrontend.components.AppScaffold
import com.example.proyectochadfrontend.data.LoginResponse
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
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Text(
                text = "MENÚ PRINCIPAL",
                color = cyberpunkCyan,
                fontFamily = Rajdhani,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = user.correo,
                color = cyberpunkPink,
                fontFamily = Rajdhani,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Rol: ${user.rol.uppercase()}",
                color = cyberpunkYellow,
                fontFamily = Rajdhani,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            when (user.rol.uppercase()) {
                "CLIENTE" -> {
                    CyberButton("Mis Reparaciones", onNavigateToReparaciones)
                    CyberButton("Solicitud - Nueva Reparación", onCrearReparacion)
                }

                "TECNICO" -> {
                    CyberButton("Reparaciones Asignadas", onNavigateToReparaciones)
                    CyberButton("Solicitudes Disponibles", onSolicitudesDisponibles)
                    CyberButton("Diagnosticar Reparación", onDiagnosticar)
                }

                "ADMIN" -> {
                    CyberButton("Todas las Reparaciones", onNavigateToReparaciones)
                    CyberButton("Solicitudes Disponibles", onAsignarTecnico)
                    CyberButton("Gestionar Servicios", onGestionServicios)
                    CyberButton("Gestionar Componentes", onGestionComponentes)
                    CyberButton("Gestionar Usuarios", onGestionUsuarios)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            IconButton(
                onClick = onLogout,
                modifier = Modifier
                    .size(60.dp)
                    .background(color = cyberpunkPink, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Cerrar sesión",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun CyberButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
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

