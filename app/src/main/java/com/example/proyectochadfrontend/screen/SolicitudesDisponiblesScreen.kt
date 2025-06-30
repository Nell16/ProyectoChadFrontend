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

@Composable
fun SolicitudesDisponiblesScreen(
    token: String,
    rol: String,
    onVolver: () -> Unit,
    onAsignarTecnico: (Long) -> Unit,
    onVerDetalle: (Long) -> Unit
) {
    var solicitudes by remember { mutableStateOf<List<ReparacionResponse>>(emptyList()) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val api = RetrofitClient.getClient(token)

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            try {
                val response = api.getReparacionesSinTecnico()
                if (response.isSuccessful) {
                    solicitudes = response.body() ?: emptyList()
                } else {
                    mensaje = "Error al cargar solicitudes"
                }
            } catch (e: Exception) {
                mensaje = "Error de red: ${e.message}"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Solicitudes Disponibles", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        mensaje?.let {
            Text("Mensaje: $it", color = MaterialTheme.colorScheme.error)
        }

        LazyColumn {
            items(solicitudes) { rep ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Equipo: ${rep.tipoEquipo} - ${rep.marca} ${rep.modelo}")
                        Text("Problema: ${rep.descripcionFalla}")
                        Text("Estado: ${rep.estado}")
                        Spacer(modifier = Modifier.height(8.dp))
                        if (rol == "ADMIN") {
                            Button(
                                onClick = { onAsignarTecnico(rep.id) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Asignar Técnico")
                            }
                        } else if (rol == "TECNICO") {
                            Button(
                                onClick = { onVerDetalle(rep.id) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Ver Solicitud")
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
            Text("Volver")
        }
    }
}
