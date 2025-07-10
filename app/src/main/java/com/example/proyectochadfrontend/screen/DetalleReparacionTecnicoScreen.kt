package com.example.proyectochadfrontend.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.sp
import com.example.proyectochadfrontend.R
import com.example.proyectochadfrontend.components.AppScaffold
import com.example.proyectochadfrontend.model.ReparacionResponse
import com.example.proyectochadfrontend.model.RetrofitClient
import com.example.proyectochadfrontend.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun DetalleReparacionTecnicoScreen(
    reparacionId: Long,
    token: String,
    onVolver: () -> Unit,
    onTomarSolicitud: () -> Unit
) {
    var reparacion by remember { mutableStateOf<ReparacionResponse?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val api = RetrofitClient.getClient(token)

    LaunchedEffect(reparacionId) {
        scope.launch(Dispatchers.IO) {
            try {
                val response = api.getReparacionPorId(reparacionId)
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        reparacion = response.body()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        error = "Error al obtener la reparación"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    error = "Error de red: ${e.message}"
                }
            }
        }
    }

    AppScaffold(
        selectedItem = "",
        onHomeClick = {},
        onProfileClick = {},
        onSettingsClick = {}
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
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = onVolver,
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
                        "Detalle de Reparación",
                        color = cyberpunkCyan,
                        fontFamily = Rajdhani,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                AnimatedVisibility(visible = reparacion?.tecnico == null, enter = fadeIn()) {
                    Button(
                        onClick = {
                            scope.launch(Dispatchers.IO) {
                                try {
                                    val response = api.autoasignarTecnico(reparacionId)
                                    if (response.isSuccessful) {
                                        withContext(Dispatchers.Main) {
                                            onTomarSolicitud()
                                        }
                                    } else {
                                        withContext(Dispatchers.Main) {
                                            error = "No se pudo tomar la solicitud"
                                        }
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        error = "Error: ${e.message}"
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = cyberpunkPink)
                    ) {
                        Text("Tomar esta Solicitud", color = Color.White, fontFamily = Rajdhani, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                error?.let {
                    Text("Error: $it", color = cyberpunkYellow, fontFamily = Rajdhani, fontWeight = FontWeight.Bold)
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    reparacion?.let { rep ->
                        CampoTexto1("Equipo", rep.tipoEquipo)
                        CampoTexto1("Marca", rep.marca)
                        CampoTexto1("Modelo", rep.modelo)
                        CampoTexto1("Descripción", rep.descripcionFalla)
                        CampoTexto1("Estado", rep.estado)
                        CampoTexto1("Fecha Ingreso", rep.fechaIngreso)
                        CampoTexto1("Cliente", rep.usuario.primerNombre)
                        CampoTexto1("Correo", rep.usuario.correo)
                        rep.diagnostico?.let { CampoTexto1("Diagnóstico", it) }
                        rep.solucion?.let { CampoTexto1("Solución", it) }
                        rep.costo?.let { CampoTexto1("Costo", "$it USD") }
                        rep.servicio?.let { CampoTexto1("Servicio", "${it.nombre} - ${it.descripcion}") }
                    }
                }
            }
        }
    }
}

@Composable
fun CampoTexto1(label: String, valor: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$label:",
            color = cyberpunkYellow,
            fontFamily = Rajdhani,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(
                color = Color.White,
                fontFamily = Rajdhani,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = cyberpunkCyan,
                focusedBorderColor = cyberpunkCyan,
                cursorColor = Color.Transparent,
                disabledTextColor = Color.White,
                focusedLabelColor = cyberpunkCyan,
                unfocusedLabelColor = cyberpunkCyan
            )
        )
        Spacer(modifier = Modifier.height(6.dp))
    }
}
