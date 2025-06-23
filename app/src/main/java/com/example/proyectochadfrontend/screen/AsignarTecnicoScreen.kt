// AsignarTecnicoScreen.kt
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
import com.example.proyectochadfrontend.data.UsuarioDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AsignarTecnicoScreen(token: String, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var reparaciones by remember { mutableStateOf<List<ReparacionResponse>>(emptyList()) }
    var tecnicos by remember { mutableStateOf<List<UsuarioDTO>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            try {
                val api = RetrofitClient.getClient(token)
                val reparacionesResp = api.getReparacionesSinTecnico()
                val tecnicosResp = api.getTecnicos()
                if (reparacionesResp.isSuccessful && tecnicosResp.isSuccessful) {
                    reparaciones = reparacionesResp.body() ?: emptyList()
                    tecnicos = tecnicosResp.body() ?: emptyList()
                } else {
                    error = "Error al cargar datos"
                }
            } catch (e: Exception) {
                error = "Error: ${e.message}"
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Asignar Técnico", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        if (error != null) {
            Text("$error", color = MaterialTheme.colorScheme.error)
        }

        LazyColumn {
            items(reparaciones) { rep ->
                Card(modifier = Modifier.padding(8.dp)) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("${rep.tipoEquipo} - ${rep.marca} ${rep.modelo}")
                        Text("Falla: ${rep.descripcionFalla}")
                        DropdownMenuSample(rep.id, tecnicos, token)
                    }
                }
            }
        }

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Volver")
        }
    }
}

@Composable
fun DropdownMenuSample(reparacionId: Long, tecnicos: List<UsuarioDTO>, token: String) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<UsuarioDTO?>(null) }
    val scope = rememberCoroutineScope()

    Column {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selected?.nombre ?: "Seleccionar Técnico")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            tecnicos.forEach { tecnico ->
                DropdownMenuItem(
                    text = { Text(tecnico.nombre) },
                    onClick = {
                        selected = tecnico
                        expanded = false
                        scope.launch(Dispatchers.IO) {
                            try {
                                val api = RetrofitClient.getClient(token)
                                api.asignarTecnico(reparacionId, tecnico.id)
                            } catch (_: Exception) {}
                        }
                    }
                )
            }
        }
    }
}
