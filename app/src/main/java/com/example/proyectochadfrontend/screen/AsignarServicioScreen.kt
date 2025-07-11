package com.example.proyectochadfrontend.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectochadfrontend.R
import com.example.proyectochadfrontend.model.*
import com.example.proyectochadfrontend.components.AppScaffold
import com.example.proyectochadfrontend.network.RetrofitClient
import com.example.proyectochadfrontend.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsignarServicioScreen(
    reparacionId: Long,
    token: String,
    onBack: () -> Unit,
    onAsignacionExitosa: () -> Unit,
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val api = RetrofitClient.getClient(token)

    var servicios by remember { mutableStateOf<List<ServicioDTO>>(emptyList()) }
    var servicioSeleccionado by remember { mutableStateOf<ServicioDTO?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var exitoAsignacion by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            try {
                val response = api.getServicios()
                if (response.isSuccessful) {
                    servicios = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error al cargar servicios", Toast.LENGTH_SHORT).show()
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
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
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
                        text = "Asignar Servicio",
                        fontSize = 24.sp,
                        color = cyberpunkCyan
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = servicioSeleccionado?.nombre ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Selecciona un servicio") },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
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
                                    if (response.isSuccessful) {
                                        withContext(Dispatchers.Main) {
                                            exitoAsignacion = true
                                        }
                                    } else {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(context, "Selecciona un servicio", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = cyberpunkCyan)
                ) {
                    Text("Asignar Servicio", color = Color.Black)
                }


                if (exitoAsignacion) {
                    AlertDialog(
                        onDismissRequest = {
                            exitoAsignacion = false
                            scope.launch {
                                onAsignacionExitosa()
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    exitoAsignacion = false
                                    scope.launch {
                                        onAsignacionExitosa()
                                    }
                                }
                            ) {
                                Text("OK", color = cyberpunkPink)
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Éxito",
                                tint = Color.Green
                            )
                        },
                        title = {
                            Text("¡Servicio asignado correctamente!", color = cyberpunkCyan)
                        },
                        text = {
                            Text("El servicio fue agregado a la reparación.")
                        },
                        containerColor = Color(0xFF1A1A1A),
                        titleContentColor = Color.White,
                        textContentColor = Color.White
                    )

                }
            }
        }
    }
}
