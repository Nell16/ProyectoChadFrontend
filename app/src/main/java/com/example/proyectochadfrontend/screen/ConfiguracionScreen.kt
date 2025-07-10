package com.example.proyectochadfrontend.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectochadfrontend.R
import com.example.proyectochadfrontend.components.AppScaffold
import com.example.proyectochadfrontend.ui.components.CyberpunkTextField
import com.example.proyectochadfrontend.ui.theme.*

@Composable
fun ConfiguracionScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    var notificaciones by remember { mutableStateOf(true) }
    var usarCyberpunk by remember { mutableStateOf(true) }
    var idioma by remember { mutableStateOf("Español") }
    var mostrarCambioContrasena by remember { mutableStateOf(false) }
    var nuevaContrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }

    val idiomasDisponibles = listOf("Español", "Inglés", "Portugués", "Francés")
    var desplegarListaIdiomas by remember { mutableStateOf(false) }

    AppScaffold(
        selectedItem = "configuracion",
        onHomeClick = onHomeClick,
        onProfileClick = onProfileClick,
        onSettingsClick = onSettingsClick
    ) {
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
                    text = "Configuración",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = cyberpunkCyan
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                SettingItem("Notificaciones", notificaciones) { notificaciones = it }
                SettingItem("Usar tema oscuro", usarCyberpunk) { usarCyberpunk = it }

                Spacer(modifier = Modifier.height(24.dp))
                Text("Idioma", color = cyberpunkYellow, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                CyberpunkTextField(
                    value = idioma,
                    onValueChange = {},
                    label = "Seleccionar Idioma",
                    modifier = Modifier
                        .clickable { desplegarListaIdiomas = !desplegarListaIdiomas }
                        .fillMaxWidth(),
                    enabled = false
                )

                if (desplegarListaIdiomas) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    ) {
                        idiomasDisponibles.forEach { lang ->
                            Text(
                                text = lang,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        idioma = lang
                                        desplegarListaIdiomas = false
                                    }
                                    .padding(8.dp),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { /* Sin funcionalidad */ },
                    colors = ButtonDefaults.buttonColors(containerColor = cyberpunkPink),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Contactar soporte", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (mostrarCambioContrasena) {
                    CyberpunkTextField(
                        value = nuevaContrasena,
                        onValueChange = { nuevaContrasena = it },
                        label = "Nueva contraseña",
                        isPassword = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CyberpunkTextField(
                        value = confirmarContrasena,
                        onValueChange = { confirmarContrasena = it },
                        label = "Confirmar contraseña",
                        isPassword = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            mostrarCambioContrasena = false
                            nuevaContrasena = ""
                            confirmarContrasena = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = cyberpunkCyan),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar", fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                } else {
                    Button(
                        onClick = { mostrarCambioContrasena = true },
                        colors = ButtonDefaults.buttonColors(containerColor = cyberpunkCyan),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cambiar Contraseña", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = cyberpunkPink),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar sesión", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun SettingItem(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontWeight = FontWeight.Bold, color = cyberpunkYellow)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = cyberpunkPink,
                uncheckedThumbColor = Color.Gray
            )
        )
    }
}
