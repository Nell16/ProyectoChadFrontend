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
import com.example.proyectochadfrontend.network.RetrofitClient
import com.example.proyectochadfrontend.model.UsuarioDTO
import com.example.proyectochadfrontend.components.AppScaffold
import com.example.proyectochadfrontend.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AsignarTecnicoScreen(
    token: String,
    reparacionId: Long,
    onBack: () -> Unit,
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var tecnicos by remember { mutableStateOf<List<UsuarioDTO>>(emptyList()) }
    var cargaReparaciones by remember { mutableStateOf<Map<Long, Int>>(emptyMap()) }
    var mensaje by remember { mutableStateOf<String?>(null) }

    val api = RetrofitClient.getClient(token)

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            try {
                val response = api.getTecnicos()
                if (response.isSuccessful) {
                    val listaTecnicos = response.body() ?: emptyList()
                    tecnicos = listaTecnicos

                    val mapeo = mutableMapOf<Long, Int>()
                    for (tecnico in listaTecnicos) {
                        val reparacionesResp = api.getReparacionesPorTecnico(tecnico.id)
                        if (reparacionesResp.isSuccessful) {
                            val activas = reparacionesResp.body()?.filter {
                                it.estado != "REPARADO" && it.estado != "ENTREGADO"
                            } ?: emptyList()
                            mapeo[tecnico.id] = activas.size
                        } else {
                            mapeo[tecnico.id] = -1
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

    AppScaffold(
        selectedItem = "",
        onHomeClick = onHomeClick,
        onProfileClick = onProfileClick,
        onSettingsClick = onSettingsClick
    ) {
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
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))

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
                        text = "Asignar Técnico",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = cyberpunkCyan
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                mensaje?.let {
                    Text(it, color = cyberpunkYellow, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(tecnicos) { tecnico ->
                        val cantidad = cargaReparaciones[tecnico.id]

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = cyberpunkSurface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Nombre: ${tecnico.primerNombre}", color = Color.White, fontWeight = FontWeight.Bold)
                                Text("Correo: ${tecnico.correo}", color = Color.White, fontWeight = FontWeight.Bold)
                                Text("Rol: ${tecnico.rol}", color = Color.White, fontWeight = FontWeight.Bold)
                                if (cantidad != null) {
                                    if (cantidad >= 0) {
                                        Text("Reparaciones activas: $cantidad", color = cyberpunkYellow)
                                        if (cantidad >= 5) {
                                            Text(
                                                text = "Este técnico ha alcanzado el límite!",
                                                color = cyberpunkPink,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    } else {
                                        Text("Error al obtener cantidad de reparaciones", color = Color.Red)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        scope.launch(Dispatchers.IO) {
                                            try {
                                                val response = api.asignarTecnico(reparacionId, tecnico.id)
                                                withContext(Dispatchers.Main) {
                                                    mensaje = if (response.isSuccessful)
                                                        "Técnico asignado correctamente"
                                                    else
                                                        "Error al asignar técnico"
                                                }
                                            } catch (e: Exception) {
                                                withContext(Dispatchers.Main) {
                                                    mensaje = "Error: ${e.message}"
                                                }
                                            }
                                        }
                                    },
                                    enabled = cantidad != null && cantidad < 5,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = cyberpunkCyan)
                                ) {
                                    Text("Asignar a esta reparación", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
