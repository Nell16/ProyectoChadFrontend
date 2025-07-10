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
import com.example.proyectochadfrontend.components.AppScaffold
import com.example.proyectochadfrontend.model.ComponenteDTO
import com.example.proyectochadfrontend.model.ComponenteRequestDTO
import com.example.proyectochadfrontend.model.RetrofitClient
import com.example.proyectochadfrontend.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsignarComponentesScreen(
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

    var componentesDisponibles by remember { mutableStateOf(listOf<ComponenteDTO>()) }
    var componenteSeleccionado by remember { mutableStateOf<ComponenteDTO?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var exitoAsignacion by remember { mutableStateOf(false) }

    // Cargar componentes
    LaunchedEffect(true) {
        try {
            val response = api.getTodosLosComponentes()
            if (response.isSuccessful) {
                componentesDisponibles = response.body() ?: emptyList()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error al cargar componentes", Toast.LENGTH_SHORT).show()
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
                        text = "Asignar Componente",
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
                        value = componenteSeleccionado?.nombre ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Selecciona un componente") },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        componentesDisponibles.forEach { componente ->
                            DropdownMenuItem(
                                text = { Text(componente.nombre) },
                                onClick = {
                                    componenteSeleccionado = componente
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val componente = componenteSeleccionado
                        if (componente != null) {
                            scope.launch(Dispatchers.IO) {
                                try {
                                    val request = ComponenteRequestDTO(
                                        nombre = componente.nombre,
                                        descripcion = componente.descripcion,
                                        precio = componente.precio,
                                        cantidad = componente.cantidad,
                                        reparacionId = reparacionId
                                    )
                                    val response = api.agregarComponente(request)
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
                            Toast.makeText(context, "Selecciona un componente", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = cyberpunkCyan)
                ) {
                    Text("Asignar Componente", color = Color.Black)
                }

                // Ventana emergente de éxito
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
                            Text("¡Asignado correctamente!", color = cyberpunkCyan)
                        },
                        text = {
                            Text("El componente fue agregado a la reparación.")
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
