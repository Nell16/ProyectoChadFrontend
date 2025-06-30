package com.example.proyectochadfrontend.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectochadfrontend.data.RetrofitClient
import com.example.proyectochadfrontend.data.ServicioDTO
import com.example.proyectochadfrontend.data.ServicioRequest
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

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Gestión de Servicios", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        mensaje?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Formulario para agregar/editar servicio
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = precio,
            onValueChange = { precio = it },
            label = { Text("Precio Base") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = {
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
            }) {
                Text(if (servicioEnEdicionId != null) "Actualizar Servicio" else "Agregar Servicio")
            }

            // Botón para cancelar edición
            if (servicioEnEdicionId != null) {
                Button(onClick = {
                    servicioEnEdicionId = null
                    nombre = ""
                    descripcion = ""
                    precio = ""
                    mensaje = "Edición cancelada"
                }) {
                    Text("Cancelar")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(servicios) { servicio ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Nombre: ${servicio.nombre}")
                        Text("Descripción: ${servicio.descripcion}")
                        Text("Precio: ${servicio.precioBase}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(onClick = {
                                nombre = servicio.nombre
                                descripcion = servicio.descripcion
                                precio = servicio.precioBase.toString()
                                servicioEnEdicionId = servicio.id
                                mensaje = "Editando servicio ID ${servicio.id}"
                            }) {
                                Text("Editar")
                            }

                            Button(onClick = {
                                scope.launch(Dispatchers.IO) {
                                    val response = api.eliminarServicio(servicio.id)
                                    withContext(Dispatchers.Main) {
                                        if (response.isSuccessful) {
                                            mensaje = "Servicio eliminado"
                                            cargarServicios()
                                        } else {
                                            mensaje = "Error al eliminar"
                                        }
                                    }
                                }
                            }) {
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Volver")
        }
    }
}

