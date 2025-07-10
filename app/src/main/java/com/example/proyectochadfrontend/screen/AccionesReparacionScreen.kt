package com.example.proyectochadfrontend.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectochadfrontend.R
import com.example.proyectochadfrontend.components.AppScaffold
import com.example.proyectochadfrontend.ui.theme.cyberpunkCyan
import com.example.proyectochadfrontend.ui.theme.cyberpunkPink

@Composable
fun AccionesReparacionScreen(
    reparacionId: Long,
    token: String,
    rol: String,
    onBack: () -> Unit,
    onAsignarServicio: (Long) -> Unit,
    onCambiarEstado: (Long) -> Unit,
    onAsignarTecnico: (Long) -> Unit,
    onVerFactura: (Long) -> Unit,
    onVerHistorial: (Long) -> Unit,
    onAsignarComponentes: (Long) -> Unit,
    onDiagnosticar: (Long) -> Unit,
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
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
                        text = "Acciones sobre Reparación",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = cyberpunkCyan
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                val buttonModifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)

                if (rol == "ADMIN") {
                    Button(
                        onClick = { onAsignarServicio(reparacionId) },
                        modifier = buttonModifier,
                        colors = ButtonDefaults.buttonColors(containerColor = cyberpunkCyan)
                    ) {
                        Text("Asignar Servicio", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onAsignarComponentes(reparacionId) },
                        modifier = buttonModifier,
                        colors = ButtonDefaults.buttonColors(containerColor = cyberpunkCyan)
                    ) {
                        Text("Asignar Componentes", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onAsignarTecnico(reparacionId) },
                        modifier = buttonModifier,
                        colors = ButtonDefaults.buttonColors(containerColor = cyberpunkCyan)
                    ) {
                        Text("Asignar Técnico", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (rol == "TECNICO") {
                    Button(
                        onClick = { onDiagnosticar(reparacionId) },
                        modifier = buttonModifier,
                        colors = ButtonDefaults.buttonColors(containerColor = cyberpunkCyan)
                    ) {
                        Text("Diagnosticar Reparación", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onAsignarComponentes(reparacionId) },
                        modifier = buttonModifier,
                        colors = ButtonDefaults.buttonColors(containerColor = cyberpunkCyan)
                    ) {
                        Text("Asignar Componentes", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onAsignarServicio(reparacionId) },
                        modifier = buttonModifier,
                        colors = ButtonDefaults.buttonColors(containerColor = cyberpunkCyan)
                    ) {
                        Text("Asignar Servicio", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (rol == "ADMIN" || rol == "TECNICO") {
                    Button(
                        onClick = { onCambiarEstado(reparacionId) },
                        modifier = buttonModifier,
                        colors = ButtonDefaults.buttonColors(containerColor = cyberpunkPink)
                    ) {
                        Text("Cambiar Estado", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onVerFactura(reparacionId) },
                        modifier = buttonModifier,
                        colors = ButtonDefaults.buttonColors(containerColor = cyberpunkPink)
                    ) {
                        Text("Ver Factura", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onVerHistorial(reparacionId) },
                        modifier = buttonModifier,
                        colors = ButtonDefaults.buttonColors(containerColor = cyberpunkPink)
                    ) {
                        Text("Ver Historial", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
