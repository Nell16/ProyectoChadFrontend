package com.example.proyectochadfrontend.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.proyectochadfrontend.data.UsuarioDTO
import com.example.proyectochadfrontend.data.RetrofitClient
import com.example.proyectochadfrontend.ui.components.CyberpunkTextField
import com.example.proyectochadfrontend.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun GestionUsuariosScreen(
    token: String,
    onBack: () -> Unit,
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val api = RetrofitClient.getClient(token)
    var usuarios by remember { mutableStateOf<List<UsuarioDTO>>(emptyList()) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = api.getTodosLosUsuarios()
                if (response.isSuccessful) {
                    usuarios = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    var filtroRol by remember { mutableStateOf("Todos") }
    var busquedaNombre by remember { mutableStateOf("") }

    val rolesDisponibles = listOf("Todos", "CLIENTE", "TECNICO", "ADMIN")

    // Cargar usuarios al iniciar
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = api.getTodosLosUsuarios()
                if (response.isSuccessful) {
                    usuarios = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                // Manejo de error
            }
        }
    }

    val usuariosFiltrados = usuarios.filter {
        (filtroRol == "Todos" || it.rol == filtroRol) &&
                (it.primerNombre + " " + it.primerApellido).contains(busquedaNombre, ignoreCase = true)
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
                        text = "Gestión de Usuarios",
                        color = cyberpunkCyan,
                        fontFamily = Rajdhani,
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    DropdownMenuRolFiltro(filtroRol, rolesDisponibles) { filtroRol = it }
                    Spacer(modifier = Modifier.width(12.dp))
                    CyberpunkTextField(
                        value = busquedaNombre,
                        onValueChange = { busquedaNombre = it },
                        label = "Buscar por nombre",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(usuariosFiltrados) { usuario ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            colors = CardDefaults.cardColors(containerColor = cyberpunkSurface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Nombre: ${usuario.primerNombre} ${usuario.primerApellido}", fontWeight = FontWeight.Bold, color = Color.White, fontFamily = Rajdhani)
                                Text("Correo: ${usuario.correo}", fontWeight = FontWeight.Bold, color = cyberpunkYellow, fontFamily = Rajdhani)
                                Text("Rol: ${usuario.rol}", fontWeight = FontWeight.Bold, color = cyberpunkYellow, fontFamily = Rajdhani)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Button(
                                        onClick = { /* editar */ },
                                        colors = ButtonDefaults.buttonColors(containerColor = cyberpunkYellow)
                                    ) {
                                        Text("Editar", color = Color.Black)
                                    }
                                    Button(
                                        onClick = { /* eliminar */ },
                                        colors = ButtonDefaults.buttonColors(containerColor = cyberpunkPink)
                                    ) {
                                        Text("Eliminar", color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    FloatingActionButton(
                        onClick = { /* abrir dialog */ },
                        containerColor = cyberpunkPink
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownMenuRolFiltro(selected: String, opciones: List<String>, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        CyberpunkTextField(
            value = selected,
            onValueChange = {},
            label = "Rol",
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
