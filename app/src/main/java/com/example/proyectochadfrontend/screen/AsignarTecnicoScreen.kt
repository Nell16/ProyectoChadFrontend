package com.example.proyectochadfrontend.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectochadfrontend.data.RetrofitClient
import com.example.proyectochadfrontend.data.UsuarioDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AsignarTecnicoScreen(
    token: String,
    reparacionId: Long,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var tecnicos by remember { mutableStateOf<List<UsuarioDTO>>(emptyList()) }
    var cargaReparaciones by remember { mutableStateOf<Map<Long, Int>>(emptyMap()) } // técnicoId -> cantidad activa
    var mensaje by remember { mutableStateOf<String?>(null) }

    val api = RetrofitClient.getClient(token)

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            try {
                val response = api.getTecnicos()
                if (response.isSuccessful) {
                    val listaTecnicos = response.body() ?: emptyList()
                    tecnicos = listaTecnicos

                    // Consultar reparaciones activas por cada técnico
                    val mapeo = mutableMapOf<Long, Int>()
                    for (tecnico in listaTecnicos) {
                        val reparacionesResp = api.getReparacionesPorTecnico(tecnico.id)
                        if (reparacionesResp.isSuccessful) {
                            val activas = reparacionesResp.body()?.filter {
                                it.estado != "REPARADO" && it.estado != "ENTREGADO"
                            } ?: emptyList()
                            mapeo[tecnico.id] = activas.size
                        } else {
                            mapeo[tecnico.id] = -1 // error al cargar
                        }
                    }

                    withContext(Dispatchers.Main) {
                        cargaReparaciones = mapeo
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        mensaje = "Error al cargar técnicos"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mensaje = "Error de red: ${e.message}"
                }
            }
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Asignar Técnico", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        if (mensaje != null) {
            Text(mensaje!!, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        LazyColumn {
            items(tecnicos) { tecnico ->
                val cantidad = cargaReparaciones[tecnico.id]

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Nombre: ${tecnico.nombre}")
                        Text("Correo: ${tecnico.correo}")
                        Text("Rol: ${tecnico.rol}")
                        if (cantidad != null) {
                            if (cantidad >= 0) {
                                Text("Reparaciones activas: $cantidad")
                                if (cantidad >= 5) {
                                    Text(
                                        text = "Este técnico ha alcanzado el limite!",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            } else {
                                Text("Error al obtener cantidad de reparaciones")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                scope.launch(Dispatchers.IO) {
                                    try {
                                        val response = api.asignarTecnico(reparacionId, tecnico.id)
                                        withContext(Dispatchers.Main) {
                                            if (response.isSuccessful) {
                                                mensaje = "Técnico asignado correctamente"
                                            } else {
                                                mensaje = "Error al asignar técnico"
                                            }
                                        }
                                    } catch (e: Exception) {
                                        withContext(Dispatchers.Main) {
                                            mensaje = "Error: ${e.message}"
                                        }
                                    }
                                }
                            },
                            enabled = cantidad != null && cantidad < 5, // 🔒 desactiva si >= 5
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Asignar a esta reparación")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Volver")
        }
    }
}
