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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectochadfrontend.R
import com.example.proyectochadfrontend.data.ReparacionResponse
import com.example.proyectochadfrontend.data.RetrofitClient
import com.example.proyectochadfrontend.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MisReparacionesScreen(
    userId: Long,
    token: String,
    onBack: () -> Unit,
    onVerDetalle: (Long) -> Unit,
    rol: String,
    onAccionesReparacion: ((Long) -> Unit)? = null
) {
    var reparaciones by remember { mutableStateOf<List<ReparacionResponse>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            try {
                val api = RetrofitClient.getClient(token)
                val response = when (rol) {
                    "TECNICO" -> api.getReparacionesPorTecnico(userId)
                    else -> api.getReparacionesPorUsuario(userId)
                }

                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()
                    val filtradas = if (rol == "TECNICO") {
                        lista.filter { it.estado != "REPARADO" && it.estado != "ENTREGADO" }
                    } else {
                        lista
                    }

                    withContext(Dispatchers.Main) {
                        reparaciones = filtradas
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        error = "Error al cargar reparaciones"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    error = "Error de red: ${e.message}"
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.pantallabackground),
            contentDescription = "Fondo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
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
                    "Mis Reparaciones",
                    color = cyberpunkCyan,
                    fontFamily = Rajdhani,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            error?.let {
                Text("Error: $it", color = cyberpunkYellow, fontFamily = Rajdhani, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(reparaciones) { rep ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = cyberpunkSurface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Equipo: ${rep.tipoEquipo} - ${rep.marca} ${rep.modelo}", color = Color.White, fontFamily = Rajdhani, fontWeight = FontWeight.Bold)
                            Text("Estado: ${rep.estado}", color = cyberpunkCyan, fontFamily = Rajdhani, fontWeight = FontWeight.Bold)
                            Text("Problema: ${rep.descripcionFalla}", color = Color.White, fontFamily = Rajdhani, fontWeight = FontWeight.Bold)
                            Text("Fecha: ${rep.fechaIngreso}", color = Color.White, fontFamily = Rajdhani, fontWeight = FontWeight.Bold)

                            rep.diagnostico?.let {
                                Text("Diagnóstico: $it", color = cyberpunkYellow, fontFamily = Rajdhani, fontWeight = FontWeight.Bold)
                            }

                            rep.solucion?.let {
                                Text("Solución: $it", color = cyberpunkPink, fontFamily = Rajdhani, fontWeight = FontWeight.Bold)
                            }

                            rep.costo?.let {
                                Text("Costo: $it USD", color = cyberpunkCyan, fontFamily = Rajdhani, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Button(
                                    onClick = { onVerDetalle(rep.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = cyberpunkCyan)
                                ) {
                                    Text("Ver Detalle", color = Color.Black, fontFamily = Rajdhani, fontWeight = FontWeight.Bold)
                                }

                                if (rol == "TECNICO" || rol == "ADMIN") {
                                    Button(
                                        onClick = { onAccionesReparacion?.invoke(rep.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = cyberpunkPink)
                                    ) {
                                        Text("Acciones", color = Color.White, fontFamily = Rajdhani, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
