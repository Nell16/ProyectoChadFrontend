package com.example.proyectochadfrontend.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectochadfrontend.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsignarServicioScreen(
    reparacionId: Long,
    token: String,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val api = RetrofitClient.getClient(token)

    var servicios by remember { mutableStateOf<List<ServicioDTO>>(emptyList()) }
    var servicioSeleccionado by remember { mutableStateOf<ServicioDTO?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf<String?>(null) }

    // Cargar servicios al inicio
    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            try {
                val response = api.getServicios()
                if (response.isSuccessful) {
                    servicios = response.body() ?: emptyList()
                } else {
                    mensaje = "Error al cargar servicios"
                }
            } catch (e: Exception) {
                mensaje = "Error de red: ${e.message}"
            }
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Asignar Servicio a Reparación", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = servicioSeleccionado?.nombre ?: "Selecciona un servicio",
                onValueChange = {},
                readOnly = true,
                label = { Text("Servicio") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                servicios.forEach { servicio ->
                    DropdownMenuItem(
                        text = { Text(servicio.nombre) },
                        onClick = {
                            servicioSeleccionado = servicio
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val selected = servicioSeleccionado
                if (selected != null) {
                    scope.launch(Dispatchers.IO) {
                        try {
                            val dto = AsignarServicioDTO(servicioId = selected.id)
                            val response = api.asignarServicio(reparacionId, dto)
                            mensaje = if (response.isSuccessful) {
                                "Servicio asignado correctamente"
                            } else {
                                "Error: ${response.code()}"
                            }
                        } catch (e: Exception) {
                            mensaje = "Error de red: ${e.message}"
                        }
                    }
                } else {
                    mensaje = "Selecciona un servicio antes de continuar"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Asignar Servicio")
        }

        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Volver")
        }

        mensaje?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(it, color = MaterialTheme.colorScheme.primary)
        }
    }
}
