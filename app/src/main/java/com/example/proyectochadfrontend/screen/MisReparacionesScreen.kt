package com.example.proyectochadfrontend.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectochadfrontend.data.ReparacionResponse
import com.example.proyectochadfrontend.data.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MisReparacionesScreen(
    userId: Long,
    token: String,
    onBack: () -> Unit,
    onVerDetalle: (Long) -> Unit,
    rol: String, // "CLIENTE" o "TECNICO"
    onAccionesReparacion: ((Long) -> Unit)? = null
) {
    var reparaciones by remember { mutableStateOf<List<ReparacionResponse>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            try {
                val api = RetrofitClient.getClient(token)
                val response = when (rol) {
                    "TECNICO" -> api.getReparacionesPorTecnico(userId)
                    else -> api.getReparacionesPorUsuario(userId)
                }

                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()
                    val filtradas = if (rol == "TECNICO") {
                        lista.filter { it.estado != "REPARADO" && it.estado != "ENTREGADO" }
                    } else {
                        lista
                    }

                    withContext(Dispatchers.Main) {
                        reparaciones = filtradas
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        error = "Error al cargar reparaciones"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    error = "Error de red: ${e.message}"
                }
            }
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Mis Reparaciones", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }

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
                        Text("Estado: ${rep.estado}")
                        Text("Problema: ${rep.descripcionFalla}")
                        Text("Fecha: ${rep.fechaIngreso}")
                        rep.diagnostico?.let { Text("Diagnóstico: $it") }
                        rep.solucion?.let { Text("Solución: $it") }
                        rep.costo?.let { Text("Costo: $it USD") }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { onVerDetalle(rep.id) }) {
                                Text("Ver Detalle")
                            }

                            if (rol == "TECNICO" || rol == "ADMIN") {
                                Button(onClick = { onAccionesReparacion?.invoke(rep.id) }) {
                                    Text("Acciones")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

