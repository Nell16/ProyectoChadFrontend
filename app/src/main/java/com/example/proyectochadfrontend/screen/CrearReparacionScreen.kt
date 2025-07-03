package com.example.proyectochadfrontend.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectochadfrontend.R
import com.example.proyectochadfrontend.data.ReparacionRequest
import com.example.proyectochadfrontend.data.RetrofitClient
import com.example.proyectochadfrontend.ui.components.CyberpunkTextField
import com.example.proyectochadfrontend.ui.theme.*
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

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.pantallabackground),
            contentDescription = "Fondo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Botón de retorno
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(42.dp)
                        .background(color = cyberpunkPink, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    "Crear Solicitud",
                    color = cyberpunkCyan,
                    fontFamily = Rajdhani,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            CyberpunkTextField(
                value = tipoEquipo,
                onValueChange = { tipoEquipo = it },
                label = "Tipo de equipo",
                modifier = Modifier.fillMaxWidth(0.95f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            CyberpunkTextField(
                value = marca,
                onValueChange = { marca = it },
                label = "Marca",
                modifier = Modifier.fillMaxWidth(0.95f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            CyberpunkTextField(
                value = modelo,
                onValueChange = { modelo = it },
                label = "Modelo",
                modifier = Modifier.fillMaxWidth(0.95f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            CyberpunkTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = "Descripción de la falla",
                modifier = Modifier.fillMaxWidth(0.95f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            CyberButton("Registrar") {
                error = null
                mensaje = null

                if (tipoEquipo.isBlank() || marca.isBlank() || modelo.isBlank() || descripcion.isBlank()) {
                    error = "Todos los campos son obligatorios"
                    return@CyberButton
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
            }

            Spacer(modifier = Modifier.height(8.dp))

            mensaje?.let {
                Text(
                    it,
                    color = cyberpunkYellow,
                    fontFamily = Rajdhani,
                    fontWeight = FontWeight.Bold
                )
            }

            error?.let {
                Text(
                    it,
                    color = cyberpunkPink,
                    fontFamily = Rajdhani,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
