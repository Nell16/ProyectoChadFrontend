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
import kotlinx.coroutines.withContext

@Composable
fun DetalleReparacionTecnicoScreen(
    reparacionId: Long,
    token: String,
    onVolver: () -> Unit,
    onTomarSolicitud: () -> Unit
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
                    withContext(Dispatchers.Main) {
                        reparacion = response.body()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        error = "Error al obtener la reparación"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    error = "Error de red: ${e.message}"
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Detalle de Reparación", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        if (error != null) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        } else if (reparacion != null) {
            val rep = reparacion!!
            Text("Equipo: ${rep.tipoEquipo}")
            Text("Marca: ${rep.marca}")
            Text("Modelo: ${rep.modelo}")
            Text("Descripción: ${rep.descripcionFalla}")
            Text("Estado: ${rep.estado}")
            Text("Fecha Ingreso: ${rep.fechaIngreso}")
            Spacer(modifier = Modifier.height(8.dp))

            Text("Cliente: ${rep.usuario.nombre}")
            Text("Correo: ${rep.usuario.correo}")

            rep.diagnostico?.let { Text("Diagnóstico: $it") }
            rep.solucion?.let { Text("Solución: $it") }
            rep.costo?.let { Text("Costo: $it USD") }
            rep.servicio?.let { Text("Servicio: ${it.nombre} - ${it.descripcion}") }

            Spacer(modifier = Modifier.height(24.dp))

            if (rep.tecnico == null) {
                Button(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            try {
                                val response = api.autoasignarTecnico(reparacionId)
                                if (response.isSuccessful) {
                                    withContext(Dispatchers.Main) {
                                        onTomarSolicitud()
                                    }
                                } else {
                                    withContext(Dispatchers.Main) {
                                        error = "No se pudo tomar la solicitud"
                                    }
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    error = "Error: ${e.message}"
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tomar esta Solicitud")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
            Text("Volver")
        }
    }
}

