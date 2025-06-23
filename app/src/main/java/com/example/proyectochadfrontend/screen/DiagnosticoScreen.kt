package com.example.proyectochadfrontend.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectochadfrontend.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun DiagnosticoScreen(
    reparacionId: Long,
    token: String,
    onBack: () -> Unit
) {
    var diagnostico by remember { mutableStateOf("") }
    var solucion by remember { mutableStateOf("") }
    var costo by remember { mutableStateOf("") }
    var componentes by remember { mutableStateOf<List<ComponenteDTO>>(emptyList()) }
    var mensaje by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val api = RetrofitClient.getClient(token)

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            try {
                val response = api.getComponentesPorReparacion(reparacionId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        componentes = it
                    }
                }
            } catch (e: Exception) {
                mensaje = "Error al cargar componentes: ${e.message}"
            }
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Diagnóstico de Reparación", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = diagnostico,
            onValueChange = { diagnostico = it },
            label = { Text("Diagnóstico") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = solucion,
            onValueChange = { solucion = it },
            label = { Text("Solución") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = costo,
            onValueChange = { costo = it },
            label = { Text("Costo (USD)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text("Componentes utilizados", style = MaterialTheme.typography.titleMedium)
        LazyColumn {
            items(componentes) { comp ->
                Text("• ${comp.nombre} - ${comp.precio} USD")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    try {
                        val body = ReparacionDiagnosticoRequest(
                            diagnostico = diagnostico,
                            solucion = solucion,
                            costo = costo.toDoubleOrNull() ?: 0.0
                        )
                        val response = api.actualizarDiagnostico(reparacionId, body)
                        mensaje = if (response.isSuccessful) {
                            "Diagnóstico actualizado exitosamente"
                        } else {
                            "Error al actualizar: ${response.code()}"
                        }
                    } catch (e: Exception) {
                        mensaje = "Error de red: ${e.message}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Diagnóstico")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Volver")
        }

        mensaje?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.primary)
        }
    }
}
