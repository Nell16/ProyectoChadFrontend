package com.example.proyectochadfrontend.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectochadfrontend.data.ReparacionRequest
import com.example.proyectochadfrontend.data.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CrearReparacionScreen(
    userId: Long,
    token: String,
    onReparacionCreada: () -> Unit,
    onBack: () -> Unit
) {
    var tipoEquipo by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    var mensaje by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val api = RetrofitClient.getClient(token)

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Nueva Reparación", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = tipoEquipo, onValueChange = { tipoEquipo = it }, label = { Text("Tipo de equipo") })
        OutlinedTextField(value = marca, onValueChange = { marca = it }, label = { Text("Marca") })
        OutlinedTextField(value = modelo, onValueChange = { modelo = it }, label = { Text("Modelo") })
        OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción de la falla") })

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                error = null
                mensaje = null

                if (tipoEquipo.isBlank() || marca.isBlank() || modelo.isBlank() || descripcion.isBlank()) {
                    error = "Todos los campos son obligatorios"
                    return@Button
                }

                val request = ReparacionRequest(
                    usuarioId = userId,
                    tipoEquipo = tipoEquipo,
                    marca = marca,
                    modelo = modelo,
                    descripcionFalla = descripcion
                )

                scope.launch(Dispatchers.IO) {
                    try {
                        val response = api.crearReparacion(request)
                        if (response.isSuccessful) {
                            mensaje = "Reparación registrada correctamente"
                            onReparacionCreada()
                        } else {
                            error = "Error al registrar: ${response.code()}"
                        }
                    } catch (e: Exception) {
                        error = "Error: ${e.message}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Volver")
        }

        mensaje?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.primary)
        }

        error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}
