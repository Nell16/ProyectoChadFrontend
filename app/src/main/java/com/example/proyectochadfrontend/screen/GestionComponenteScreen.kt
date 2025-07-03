package com.example.proyectochadfrontend.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.proyectochadfrontend.data.ComponenteDTO
import com.example.proyectochadfrontend.data.ComponenteGeneralDTO
import com.example.proyectochadfrontend.data.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun GestionComponentesScreen(
    token: String,
    onBack: () -> Unit
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

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Gestión de Componentes", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        mensaje?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Formulario
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre", color = Color.Black) },
            textStyle = LocalTextStyle.current.copy(color = Color.Black),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripcion", color = Color.Black) },
            textStyle = LocalTextStyle.current.copy(color = Color.Black),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = precio,
            onValueChange = { precio = it },
            label = { Text("precio", color = Color.Black) },
            textStyle = LocalTextStyle.current.copy(color = Color.Black),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = cantidad,
            onValueChange = { cantidad = it },
            label = { Text("cantidad", color = Color.Black) },
            textStyle = LocalTextStyle.current.copy(color = Color.Black),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = {
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
            }) {
                Text(if (componenteEnEdicionId != null) "Actualizar Componente" else "Agregar Componente")
            }

            if (componenteEnEdicionId != null) {
                Button(onClick = {
                    componenteEnEdicionId = null
                    nombre = ""
                    descripcion = ""
                    precio = ""
                    cantidad = ""
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
            items(componentes) { componente ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Nombre: ${componente.nombre}")
                        Text("Descripción: ${componente.descripcion}")
                        Text("Precio: ${componente.precio}")
                        Text("Cantidad: ${componente.cantidad}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Button(onClick = {
                                nombre = componente.nombre
                                descripcion = componente.descripcion
                                precio = componente.precio.toString()
                                cantidad = componente.cantidad.toString()
                                componenteEnEdicionId = componente.id
                                mensaje = "Editando componente ID ${componente.id}"
                            }) {
                                Text("Editar")
                            }

                            Button(onClick = {
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
