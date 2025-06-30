package com.example.proyectochadfrontend.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AccionesReparacionScreen(
    reparacionId: Long,
    token: String,
    onBack: () -> Unit,
    onAsignarServicio: (Long) -> Unit,
    onCambiarEstado: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Acciones sobre Reparación", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onAsignarServicio(reparacionId) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Asignar Servicio")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { onCambiarEstado(reparacionId) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cambiar Estado")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}
