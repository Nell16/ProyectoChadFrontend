package com.example.proyectochadfrontend.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.proyectochadfrontend.R
import com.example.proyectochadfrontend.components.AppScaffold
import com.example.proyectochadfrontend.model.LoginResponse
import com.example.proyectochadfrontend.network.RetrofitClient
import com.example.proyectochadfrontend.model.UsuarioDTO
import com.example.proyectochadfrontend.ui.components.CyberpunkTextField
import com.example.proyectochadfrontend.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream

@Composable
fun PerfilScreen(
    usuario: LoginResponse,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val api = RetrofitClient.getClient(usuario.token)

    var fotoUrl by remember { mutableStateOf(usuario.fotoPerfilUrl ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var mensaje by remember { mutableStateOf<String?>(null) }

    var primerNombre by remember { mutableStateOf(usuario.primerNombre) }
    var segundoNombre by remember { mutableStateOf(usuario.segundoNombre.orEmpty()) }
    var primerApellido by remember { mutableStateOf(usuario.primerApellido) }
    var segundoApellido by remember { mutableStateOf(usuario.segundoApellido.orEmpty()) }
    var correo by remember { mutableStateOf(usuario.correo) }
    var telefono by remember { mutableStateOf(usuario.telefono) }

    var modoEdicion by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            scope.launch(Dispatchers.IO) {
                try {
                    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                    val bytes = inputStream?.readBytes()
                    inputStream?.close()

                    if (bytes != null) {
                        val requestBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
                        val multipart = MultipartBody.Part.createFormData(
                            name = "archivo",
                            filename = "foto_${usuario.idUsuario}.jpg",
                            body = requestBody
                        )
                        val response = api.subirFotoPerfil(usuario.idUsuario, multipart)
                        fotoUrl = response.body()?.replace("\"", "") ?: ""
                    }
                } catch (e: Exception) {
                    mensaje = "Error: ${e.message}"
                }
            }
        }
    }

    AppScaffold(
        selectedItem = "perfil",
        onHomeClick = onHomeClick,
        onProfileClick = onProfileClick,
        onSettingsClick = onSettingsClick
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = rememberAsyncImagePainter(R.drawable.pantallabackground),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(42.dp)
                            .background(cyberpunkPink, CircleShape)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Perfil de Usuario",
                        color = cyberpunkCyan,
                        fontFamily = Rajdhani,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(cyberpunkSurface)
                        .clickable(enabled = modoEdicion) { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        selectedImageUri != null -> {
                            Image(
                                painter = rememberAsyncImagePainter(selectedImageUri),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        fotoUrl.isNotEmpty() -> {
                            Image(
                                painter = rememberAsyncImagePainter(fotoUrl),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        else -> {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                mensaje?.let {
                    Text(
                        text = it,
                        color = cyberpunkYellow,
                        fontFamily = Rajdhani,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                CyberpunkTextField(
                    value = primerNombre,
                    onValueChange = { primerNombre = it },
                    label = "Primer Nombre",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = modoEdicion
                )
                Spacer(modifier = Modifier.height(8.dp))
                CyberpunkTextField(
                    value = segundoNombre,
                    onValueChange = { segundoNombre = it },
                    label = "Segundo Nombre",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = modoEdicion
                )
                Spacer(modifier = Modifier.height(8.dp))
                CyberpunkTextField(
                    value = primerApellido,
                    onValueChange = { primerApellido = it },
                    label = "Primer Apellido",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = modoEdicion
                )
                Spacer(modifier = Modifier.height(8.dp))
                CyberpunkTextField(
                    value = segundoApellido,
                    onValueChange = { segundoApellido = it },
                    label = "Segundo Apellido",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = modoEdicion
                )
                Spacer(modifier = Modifier.height(8.dp))
                CyberpunkTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = "Correo Electrónico",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = modoEdicion
                )
                Spacer(modifier = Modifier.height(8.dp))
                CyberpunkTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = "Teléfono",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = modoEdicion
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            if (modoEdicion) {
                                scope.launch(Dispatchers.IO) {
                                    try {
                                        val actualizado = UsuarioDTO(
                                            id = usuario.idUsuario,
                                            primerNombre = primerNombre,
                                            segundoNombre = segundoNombre,
                                            primerApellido = primerApellido,
                                            segundoApellido = segundoApellido,
                                            correo = correo,
                                            telefono = telefono,
                                            fotoPerfilUrl = fotoUrl,
                                            rol = usuario.rol
                                        )
                                        val response = api.actualizarPerfil(usuario.idUsuario, actualizado)
                                        mensaje = if (response.isSuccessful) "Perfil actualizado correctamente" else "Error al actualizar: ${response.code()}"
                                    } catch (e: Exception) {
                                        mensaje = "Error: ${e.message}"
                                    }
                                }
                            }
                            modoEdicion = !modoEdicion
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = cyberpunkYellow),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (modoEdicion) "Guardar cambios" else "Editar",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(containerColor = cyberpunkPink),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cerrar sesión", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
