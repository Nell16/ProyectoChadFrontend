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
import com.example.proyectochadfrontend.R
import com.example.proyectochadfrontend.components.AppScaffold
import com.example.proyectochadfrontend.data.*
import com.example.proyectochadfrontend.components.AppScaffold
import com.example.proyectochadfrontend.ui.components.CyberpunkTextField
import com.example.proyectochadfrontend.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun GestionComponentesScreen(
    token: String,
    onBack: () -> Unit,
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val api = RetrofitClient.getClient(token)

    var componentes by remember { mutableStateOf<List<ComponenteDTO>>(emptyList()) }
    var mensaje by remember { mutableStateOf<String?>(null) }

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var componenteEnEdicionId by remember { mutableStateOf<Long?>(null) }

    fun cargarComponentes() {
        scope.launch(Dispatchers.IO) {
            try {
                val response = api.getTodosLosComponentes()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        componentes = response.body() ?: emptyList()
                    } else {
                        mensaje = "Error al cargar componentes"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mensaje = "Error de red: ${e.message}"
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        cargarComponentes()
    }

    AppScaffold(
        selectedItem = "",
        onHomeClick = onHomeClick,
        onProfileClick = onProfileClick,
        onSettingsClick = onSettingsClick,
        content = {
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
                        .padding(top = 32.dp) // <-- bajamos el título y botón
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
                            text = "Gestión de Componentes",
                            color = cyberpunkCyan,
                            fontFamily = Rajdhani,
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    mensaje?.let {
                        Text(it, color = cyberpunkYellow, fontFamily = Rajdhani)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    CyberpunkTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = "Nombre",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CyberpunkTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = "Descripción",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CyberpunkTextField(
                        value = precio,
                        onValueChange = { precio = it },
                        label = "Precio",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CyberpunkTextField(
                        value = cantidad,
                        onValueChange = { cantidad = it },
                        label = "Cantidad",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                scope.launch(Dispatchers.IO) {
                                    try {
                                        val dto = ComponenteGeneralDTO(
                                            nombre = nombre,
                                            descripcion = descripcion,
                                            precio = precio.toDouble(),
                                            cantidad = cantidad.toInt()
                                        )

                                        val response = if (componenteEnEdicionId != null) {
                                            api.actualizarComponenteGeneral(componenteEnEdicionId!!, dto)
                                        } else {
                                            api.crearComponenteGeneral(dto)
                                        }

                                        withContext(Dispatchers.Main) {
                                            if (response.isSuccessful) {
                                                mensaje = if (componenteEnEdicionId != null)
                                                    "Componente actualizado correctamente"
                                                else
                                                    "Componente creado correctamente"

                                                nombre = ""
                                                descripcion = ""
                                                precio = ""
                                                cantidad = ""
                                                componenteEnEdicionId = null
                                                cargarComponentes()
                                            } else {
                                                mensaje = "Error al guardar componente"
                                            }
                                        }
                                    } catch (e: Exception) {
                                        withContext(Dispatchers.Main) {
                                            mensaje = "Error: ${e.message}"
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = cyberpunkCyan)
                        ) {
                            Text(
                                text = if (componenteEnEdicionId != null) "Actualizar Componente" else "Agregar Componente",
                                color = Color.Black
                            )
                        }

                        if (componenteEnEdicionId != null) {
                            Button(
                                onClick = {
                                    componenteEnEdicionId = null
                                    nombre = ""
                                    descripcion = ""
                                    precio = ""
                                    cantidad = ""
                                    mensaje = "Edición cancelada"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = cyberpunkPink)
                            ) {
                                Text("Cancelar", color = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = cyberpunkSurface)
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(componentes) { componente ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = cyberpunkSurface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Nombre: ${componente.nombre}", fontWeight = FontWeight.Bold, color = Color.White, fontFamily = Rajdhani)
                                    Text("Descripción: ${componente.descripcion}", fontWeight = FontWeight.Bold, color = Color.White, fontFamily = Rajdhani)
                                    Text("Precio: ${componente.precio}", fontWeight = FontWeight.Bold, color = cyberpunkYellow, fontFamily = Rajdhani)
                                    Text("Cantidad: ${componente.cantidad}", fontWeight = FontWeight.Bold, color = cyberpunkYellow, fontFamily = Rajdhani)

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Button(
                                            onClick = {
                                                nombre = componente.nombre
                                                descripcion = componente.descripcion
                                                precio = componente.precio.toString()
                                                cantidad = componente.cantidad.toString()
                                                componenteEnEdicionId = componente.id
                                                mensaje = "Editando componente ID ${componente.id}"
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = cyberpunkYellow)
                                        ) {
                                            Text("Editar", color = Color.Black)
                                        }

                                        Button(
                                            onClick = {
                                                scope.launch(Dispatchers.IO) {
                                                    val response = api.eliminarComponente(componente.id)
                                                    withContext(Dispatchers.Main) {
                                                        if (response.isSuccessful) {
                                                            mensaje = "Componente eliminado"
                                                            cargarComponentes()
                                                        } else {
                                                            mensaje = "Error al eliminar"
                                                        }
                                                    }
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = cyberpunkPink)
                                        ) {
                                            Text("Eliminar", color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}
