package com.example.proyectochadfrontend.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectochadfrontend.R
import com.example.proyectochadfrontend.components.AppScaffold
import com.example.proyectochadfrontend.model.*
import com.example.proyectochadfrontend.ui.theme.*
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
                    componentes = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                mensaje = "Error al cargar componentes: ${e.message}"
            }
        }
    }

    AppScaffold(
        selectedItem = "",
        onHomeClick = {},
        onProfileClick = {},
        onSettingsClick = {}
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.pantallabackground),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
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
                        "Diagnóstico de Reparación",
                        color = cyberpunkCyan,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Rajdhani
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = diagnostico,
                    onValueChange = { diagnostico = it },
                    label = { Text("Diagnóstico", fontWeight = FontWeight.Bold, color = cyberpunkCyan) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.White, fontWeight = FontWeight.Bold),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = cyberpunkCyan,
                        unfocusedBorderColor = cyberpunkCyan,
                        cursorColor = cyberpunkPink
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = solucion,
                    onValueChange = { solucion = it },
                    label = { Text("Solución", fontWeight = FontWeight.Bold, color = cyberpunkCyan) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.White, fontWeight = FontWeight.Bold),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = cyberpunkCyan,
                        unfocusedBorderColor = cyberpunkCyan,
                        cursorColor = cyberpunkPink
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = costo,
                    onValueChange = { costo = it },
                    label = { Text("Costo (USD)", fontWeight = FontWeight.Bold, color = cyberpunkCyan) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.White, fontWeight = FontWeight.Bold),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = cyberpunkCyan,
                        unfocusedBorderColor = cyberpunkCyan,
                        cursorColor = cyberpunkPink
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Componentes Utilizados:",
                    color = cyberpunkYellow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = Rajdhani
                )

                LazyColumn(modifier = Modifier.heightIn(max = 160.dp)) {
                    items(componentes) { comp ->
                        Text(
                            text = "• ${comp.nombre} - ${comp.precio} USD",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Rajdhani
                        )
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
                                    "Diagnóstico guardado correctamente"
                                } else {
                                    "Error al guardar: ${response.code()}"
                                }
                            } catch (e: Exception) {
                                mensaje = "Error: ${e.message}"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = cyberpunkCyan)
                ) {
                    Text("Guardar Diagnóstico", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = cyberpunkPink)
                ) {
                    Text("Volver", color = Color.White, fontWeight = FontWeight.Bold)
                }

                mensaje?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(it, color = cyberpunkYellow, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
