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
    onVolver: () -> Unit
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

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Solicitudes Disponibles", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        if (mensaje != null) {
            Text("Mensaje: $mensaje", color = MaterialTheme.colorScheme.error)
        }

        LazyColumn {
            items(solicitudes) { rep ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Equipo: ${rep.tipoEquipo} - ${rep.marca} ${rep.modelo}")
                        Text("Problema: ${rep.descripcionFalla}")
                        Text("Estado: ${rep.estado}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                scope.launch(Dispatchers.IO) {
                                    try {
                                        val autoResponse = api.autoasignarTecnico(rep.id)
                                        if (autoResponse.isSuccessful) {
                                            mensaje = "Reparación asignada correctamente"
                                            solicitudes = solicitudes.filter { it.id != rep.id }
                                        } else if (autoResponse.code() == 400) {
                                            mensaje = "No puedes tomar más de 5 reparaciones activas"
                                        } else {
                                            mensaje = "Error al asignar reparación"
                                        }
                                    } catch (e: Exception) {
                                        mensaje = "Error: ${e.message}"
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Tomar esta reparación")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}
