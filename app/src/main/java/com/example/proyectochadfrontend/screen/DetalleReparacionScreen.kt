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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectochadfrontend.R
import com.example.proyectochadfrontend.components.AppScaffold
import com.example.proyectochadfrontend.model.ComponenteDTO
import com.example.proyectochadfrontend.model.ReparacionResponse
import com.example.proyectochadfrontend.model.RetrofitClient
import com.example.proyectochadfrontend.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun DetalleReparacionClienteScreen(
    reparacionId: Long,
    token: String,
    onVolver: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val api = RetrofitClient.getClient(token)

    var reparacion by remember { mutableStateOf<ReparacionResponse?>(null) }
    var componentes by remember { mutableStateOf<List<ComponenteDTO>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }

    val cargarDatos = rememberUpdatedState(newValue = {
        scope.launch(Dispatchers.IO) {
            try {
                val response = api.getReparacionPorId(reparacionId)
                if (response.isSuccessful) {
                    reparacion = response.body()
                } else {
                    error = "Error al obtener la reparación"
                }

                val compResponse = api.getComponentesPorReparacion(reparacionId)
                if (compResponse.isSuccessful) {
                    componentes = compResponse.body() ?: emptyList()
                }
            } catch (e: Exception) {
                error = "Error de red: ${e.message}"
            }
        }
    })

    LaunchedEffect(Unit) {
        cargarDatos.value()
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

            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp)
            )
            {
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

                error?.let {
                    Text(
                        "Error: $it",
                        color = cyberpunkYellow,
                        fontFamily = Rajdhani,
                        fontWeight = FontWeight.Bold
                    )
                }

                AnimatedVisibility(visible = reparacion != null, enter = fadeIn()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        reparacion?.let { rep ->
                            CampoTexto("Equipo", rep.tipoEquipo)
                            CampoTexto("Marca", rep.marca)
                            CampoTexto("Modelo", rep.modelo)
                            CampoTexto("Descripción de Falla", rep.descripcionFalla)
                            CampoTexto("Estado", rep.estado)
                            CampoTexto("Fecha de Ingreso", rep.fechaIngreso)
                            rep.diagnostico?.let { CampoTexto("Diagnóstico", it) }
                            rep.solucion?.let { CampoTexto("Solución", it) }
                            rep.costo?.let { CampoTexto("Costo", "$it USD") }
                            rep.tecnico?.let { CampoTexto("Técnico Asignado", it.primerNombre) }
                            rep.servicio?.let {
                                CampoTexto("Servicio", "${it.nombre} - ${it.descripcion}")
                            }

                            if (componentes.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Componentes Asignados:",
                                    color = cyberpunkCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    fontFamily = Rajdhani
                                )
                                componentes.forEach {
                                    CampoTexto(
                                        label = it.nombre,
                                        valor = "${it.descripcion} - ${it.cantidad} x $${it.precio}"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CampoTexto(label: String, valor: String) {
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
