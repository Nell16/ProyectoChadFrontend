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
import com.example.proyectochadfrontend.model.*
import com.example.proyectochadfrontend.network.RetrofitClient
import com.example.proyectochadfrontend.ui.components.CyberpunkTextField
import com.example.proyectochadfrontend.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun GestionServiciosScreen(
    token: String,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val api = RetrofitClient.getClient(token)

    var servicios by remember { mutableStateOf<List<ServicioDTO>>(emptyList()) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var servicioEnEdicionId by remember { mutableStateOf<Long?>(null) }

    fun cargarServicios() {
        scope.launch(Dispatchers.IO) {
            try {
                val response = api.getServicios()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        servicios = response.body() ?: emptyList()
                    } else {
                        mensaje = "Error al cargar servicios"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mensaje = "Error de red: ${e.message}"
                }
            }
        }
    }

    LaunchedEffect(Unit) { cargarServicios() }

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
                    text = "Gestión de Servicios",
                    color = cyberpunkCyan,
                    fontFamily = Rajdhani,
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            try {
                                val nuevo = ServicioRequest(nombre, descripcion, precio.toDouble())
                                val response = if (servicioEnEdicionId != null) {
                                    api.actualizarServicio(servicioEnEdicionId!!, nuevo)
                                } else {
                                    api.crearServicio(nuevo)
                                }

                                withContext(Dispatchers.Main) {
                                    if (response.isSuccessful) {
                                        mensaje = if (servicioEnEdicionId != null)
                                            "Servicio actualizado correctamente"
                                        else
                                            "Servicio creado correctamente"
                                        nombre = ""
                                        descripcion = ""
                                        precio = ""
                                        servicioEnEdicionId = null
                                        cargarServicios()
                                    } else {
                                        mensaje = "Error al guardar el servicio"
                                    }
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    mensaje = "Error: ${e.message}"
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = cyberpunkCyan,
                        contentColor = Color.Black
                    )
                ) {
                    Text(if (servicioEnEdicionId != null) "Actualizar Servicio" else "Agregar Servicio")
                }

                if (servicioEnEdicionId != null) {
                    Button(
                        onClick = {
                            servicioEnEdicionId = null
                            nombre = ""
                            descripcion = ""
                            precio = ""
                            mensaje = "Edición cancelada"
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = cyberpunkPink,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Cancelar")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Divider(color = cyberpunkSurface)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(servicios) { servicio ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = cyberpunkSurface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Nombre: ${servicio.nombre}", color = Color.White, fontFamily = Rajdhani, fontWeight = FontWeight.Bold)
                            Text("Descripción: ${servicio.descripcion}", color = Color.White, fontFamily = Rajdhani, fontWeight = FontWeight.Bold)
                            Text("Precio: ${servicio.precioBase}", color = cyberpunkYellow, fontFamily = Rajdhani, fontWeight = FontWeight.Bold)

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Button(
                                    onClick = {
                                        nombre = servicio.nombre
                                        descripcion = servicio.descripcion
                                        precio = servicio.precioBase.toString()
                                        servicioEnEdicionId = servicio.id
                                        mensaje = "Editando servicio ID ${servicio.id}"
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = cyberpunkYellow)
                                ) {
                                    Text("Editar", color = Color.Black)
                                }

                                Button(
                                    onClick = {
                                        scope.launch(Dispatchers.IO) {
                                            val response = api.eliminarServicio(servicio.id)
                                            withContext(Dispatchers.Main) {
                                                if (response.isSuccessful) {
                                                    mensaje = "Servicio eliminado"
                                                    cargarServicios()
                                                } else {
                                                    mensaje = "Error al eliminar, servicio en uso!"
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
