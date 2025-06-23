package com.example.proyectochadfrontend.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.proyectochadfrontend.data.ReparacionResponse
import com.example.proyectochadfrontend.data.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun MisReparacionesScreen(userId: Long, token: String, onBack: () -> Unit) {
    var reparaciones by remember { mutableStateOf<List<ReparacionResponse>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            try {
                val api = RetrofitClient.getClient(token)
                val response = api.getReparacionesPorUsuario(userId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        reparaciones = it
                    }
                } else {
                    error = "Error al cargar reparaciones"
                }
            } catch (e: Exception) {
                error = "Error de red: ${e.message}"
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Mis Reparaciones", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        if (error != null) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        }

        LazyColumn {
            items(reparaciones) { rep ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Equipo: ${rep.tipoEquipo} - ${rep.marca} ${rep.modelo}")
                        Text("Estado: ${rep.estado}", color = when (rep.estado) {
                            "PENDIENTE" -> Color.Gray
                            "REVISION" -> Color.Blue
                            "REPARADO" -> Color(0xFF4CAF50)
                            else -> Color.Black
                        })
                        Text("Problema: ${rep.descripcionFalla}")
                        Text("Fecha: ${rep.fechaIngreso}")
                        rep.tecnico?.let { Text("Técnico: ${it.nombre}") }
                        rep.servicio?.let { Text("Servicio: ${it.nombre} - ${it.descripcion}") }
                        rep.diagnostico?.let { Text("Diagnóstico: $it") }
                        rep.solucion?.let { Text("Solución: $it") }
                        rep.costo?.let { Text("Costo: $it USD") }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Volver")
        }
    }
}
