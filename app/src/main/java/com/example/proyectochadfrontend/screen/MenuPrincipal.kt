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
    onNavigateToReparaciones: () -> Unit
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

            Button(
                onClick = onNavigateToReparaciones,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Mis Reparaciones")
            }

            // Futuro: Botón para crear nueva reparación
            // Button(
            //     onClick = { onNavigateToCrearReparacion() },
            //     modifier = Modifier.fillMaxWidth()
            // ) {
            //     Text("Crear Solicitud de Reparación")
            // }
        }

        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        ) {
            Text("Cerrar Sesión")
        }
    }
}
