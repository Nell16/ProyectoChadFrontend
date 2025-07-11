package com.example.proyectochadfrontend.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.proyectochadfrontend.R
import com.example.proyectochadfrontend.components.AppScaffold
import com.example.proyectochadfrontend.model.ReparacionResponse
import com.example.proyectochadfrontend.network.RetrofitClient
import com.example.proyectochadfrontend.ui.components.CyberpunkTextField
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
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAccionesReparacion: ((Long) -> Unit)? = null
) {
    val scope = rememberCoroutineScope()
    var reparaciones by remember { mutableStateOf<List<ReparacionResponse>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    var filtroEstado by remember { mutableStateOf("Todos") }
    var filtroUsuario by remember { mutableStateOf("Todos") }
    var busquedaEquipo by remember { mutableStateOf("") }
    var busquedaUsuario by remember { mutableStateOf("") }

    val estadosDisponibles = listOf("Todos", "PENDIENTE", "REVISION", "REPARADO", "ENTREGADO")
    val rolesUsuarios = listOf("Todos", "CLIENTE", "TECNICO")

    LaunchedEffect(filtroEstado, filtroUsuario, busquedaEquipo, busquedaUsuario) {
        scope.launch(Dispatchers.IO) {
            try {
                val api = RetrofitClient.getClient(token)
                val response = when (rol) {
                    "ADMIN" -> api.obtenerTodasLasReparaciones()
                    "TECNICO" -> api.getReparacionesPorTecnico(userId)
                    else -> api.getReparacionesPorUsuario(userId)
                }

                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()
                    val filtradas = lista.filter {
                        (filtroEstado == "Todos" || it.estado == filtroEstado) &&
                                (filtroUsuario == "Todos" || it.usuario?.rol == filtroUsuario) &&
                                it.tipoEquipo.contains(busquedaEquipo, ignoreCase = true) &&
                                (it.usuario?.primerNombre?.contains(busquedaUsuario, ignoreCase = true) == true ||
                                        it.usuario?.primerApellido?.contains(busquedaUsuario, ignoreCase = true) == true)
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
                    .padding(top = 32.dp)
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
                        text = "Reparaciones",
                        color = cyberpunkCyan,
                        fontFamily = Rajdhani,
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    DropdownFiltro("Estado", filtroEstado, estadosDisponibles) { filtroEstado = it }
                    Spacer(modifier = Modifier.width(12.dp))
                    CyberpunkTextField(
                        value = busquedaEquipo,
                        onValueChange = { busquedaEquipo = it },
                        label = "Buscar por equipo",
                        modifier = Modifier.weight(1f)
                    )
                }

                if (rol == "ADMIN") {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        DropdownFiltro("Usuario", filtroUsuario, rolesUsuarios) { filtroUsuario = it }
                        Spacer(modifier = Modifier.width(12.dp))
                        CyberpunkTextField(
                            value = busquedaUsuario,
                            onValueChange = { busquedaUsuario = it },
                            label = "Buscar por usuario",
                            modifier = Modifier.weight(1f)
                        )
                    }
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

                                rep.usuario?.let {
                                    Text("Cliente: ${it.primerNombre} ${it.primerApellido}", color = cyberpunkYellow, fontFamily = Rajdhani, fontWeight = FontWeight.Bold)
                                }

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
}

@Composable
fun DropdownFiltro(
    label: String,
    selected: String,
    opciones: List<String>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        CyberpunkTextField(
            value = selected,
            onValueChange = {},
            label = label,
            modifier = Modifier
                .width(150.dp)
                .clickable { expanded = true },
            enabled = false
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSelected(opcion)
                        expanded = false
                    }
                )
            }
        }
    }
}


