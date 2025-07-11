package com.example.proyectochadfrontend.screen

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyectochadfrontend.components.AppScaffold
import com.example.proyectochadfrontend.model.ReparacionResponse
import com.example.proyectochadfrontend.network.RetrofitClient
import com.example.proyectochadfrontend.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SolicitudesDisponiblesScreen(
    token: String,
    rol: String,
    onVolver: () -> Unit,
    onAsignarTecnico: (Long) -> Unit,
    onVerDetalle: (Long) -> Unit,
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    var solicitudes by remember { mutableStateOf<List<ReparacionResponse>>(emptyList()) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val api = RetrofitClient.getClient(token)

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            try {
                val response = api.getReparacionesSinTecnico()
                if (response.isSuccessful) {
                    solicitudes = response.body() ?: emptyList()
                } else {
                    mensaje = "Error al cargar solicitudes"
                }
            } catch (e: Exception) {
                mensaje = "Error de red: ${e.message}"
            }
        }
    }

    AppScaffold(
        selectedItem = "",
        onHomeClick = onHomeClick,
        onProfileClick = onProfileClick,
        onSettingsClick = onSettingsClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onVolver,
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
                    text = "Solicitudes Disponibles",
                    color = cyberpunkCyan,
                    fontFamily = Rajdhani,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            mensaje?.let {
                Text(
                    text = it,
                    color = cyberpunkYellow,
                    fontFamily = Rajdhani,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(solicitudes) { rep ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = cyberpunkSurface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Equipo: ${rep.tipoEquipo} - ${rep.marca} ${rep.modelo}",
                                color = Color.White,
                                fontFamily = Rajdhani,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Problema: ${rep.descripcionFalla}",
                                color = Color.White,
                                fontFamily = Rajdhani,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Estado: ${rep.estado}",
                                color = cyberpunkYellow,
                                fontFamily = Rajdhani,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            if (rol == "ADMIN") {
                                Button(
                                    onClick = { onAsignarTecnico(rep.id) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = cyberpunkCyan,
                                        contentColor = Color.Black
                                    )
                                ) {
                                    Text(
                                        text = "Asignar Técnico",
                                        fontFamily = Rajdhani,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else if (rol == "TECNICO") {
                                Button(
                                    onClick = { onVerDetalle(rep.id) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = cyberpunkCyan,
                                        contentColor = Color.Black
                                    )
                                ) {
                                    Text(
                                        text = "Ver Solicitud",
                                        fontFamily = Rajdhani,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
