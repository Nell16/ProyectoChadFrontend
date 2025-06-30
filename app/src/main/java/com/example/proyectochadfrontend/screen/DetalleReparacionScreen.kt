package com.example.proyectochadfrontend.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectochadfrontend.data.ReparacionResponse
import com.example.proyectochadfrontend.data.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun DetalleReparacionClienteScreen(
    reparacionId: Long,
    token: String,
    onVolver: () -> Unit
) {
    var reparacion by remember { mutableStateOf<ReparacionResponse?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val api = RetrofitClient.getClient(token)

    LaunchedEffect(reparacionId) {
        scope.launch(Dispatchers.IO) {
            try {
                val response = api.getReparacionPorId(reparacionId)
                if (response.isSuccessful) {
                    reparacion = response.body()
                } else {
                    error = "Error al obtener la reparación"
                }
            } catch (e: Exception) {
                error = "Error de red: ${e.message}"
            }
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Detalle de Reparación", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        if (error != null) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        } else if (reparacion != null) {
            val rep = reparacion!!
            Text("Equipo: ${rep.tipoEquipo}")
            Text("Marca: ${rep.marca}")
            Text("Modelo: ${rep.modelo}")
            Text("Descripción de la Falla: ${rep.descripcionFalla}")
            Text("Estado: ${rep.estado}")
            Text("Fecha Ingreso: ${rep.fechaIngreso}")
            rep.diagnostico?.let { Text("Diagnóstico: $it") }
            rep.solucion?.let { Text("Solución: $it") }
            rep.costo?.let { Text("Costo: $it USD") }
            rep.tecnico?.let { Text("Técnico Asignado: ${it.nombre}") }
            rep.servicio?.let { Text("Servicio: ${it.nombre} - ${it.descripcion}") }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}
