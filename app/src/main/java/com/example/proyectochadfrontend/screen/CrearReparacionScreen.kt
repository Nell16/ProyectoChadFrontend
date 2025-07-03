package com.example.proyectochadfrontend.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Nueva Reparación",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(24.dp))

            inputField(tipoEquipo, "Tipo de equipo") { tipoEquipo = it }
            inputField(marca, "Marca") { marca = it }
            inputField(modelo, "Modelo") { modelo = it }
            inputField(descripcion, "Descripción de la falla") { descripcion = it }

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
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Text("Registrar")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onBack, modifier = Modifier.fillMaxWidth(0.9f)) {
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
}

@Composable
fun inputField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(label, color = Color.Black, fontWeight = FontWeight.SemiBold)
        },
        textStyle = LocalTextStyle.current.copy(
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        ),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(vertical = 6.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Gray,
            cursorColor = Color.Black
        )
    )
}
