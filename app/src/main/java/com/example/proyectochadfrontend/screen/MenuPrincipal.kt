package com.example.proyectochadfrontend.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectochadfrontend.data.LoginResponse

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
    //onGestionComponentes: () -> Unit = {},
    //onGestionTecnicos: () -> Unit = {},
    //onGestionReparaciones: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Bienvenido, ${user.correo}",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Rol: ${user.rol}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            when (user.rol.uppercase()) {
                "CLIENTE" -> {
                    Button(onClick = onNavigateToReparaciones, modifier = Modifier.fillMaxWidth()) {
                        Text("Mis Reparaciones")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = onCrearReparacion, modifier = Modifier.fillMaxWidth()) {
                        Text("Solicitud - Nueva Reparación")
                    }
                }

                "TECNICO" -> {
                    Button(onClick = onNavigateToReparaciones, modifier = Modifier.fillMaxWidth()) {
                        Text("Reparaciones Asignadas")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = onSolicitudesDisponibles, modifier = Modifier.fillMaxWidth()) {
                        Text("Solicitudes Disponibles")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = onDiagnosticar, modifier = Modifier.fillMaxWidth()) {
                        Text("Diagnosticar Reparación")
                    }
                }

                "ADMIN" -> {
                    Button(
                        onClick = onNavigateToReparaciones,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Todas las Reparaciones")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onAsignarTecnico,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Solicitudes Disponibles")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onGestionServicios,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Gestionar Servicios")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
            Text("Cerrar Sesión")
        }
    }
}

