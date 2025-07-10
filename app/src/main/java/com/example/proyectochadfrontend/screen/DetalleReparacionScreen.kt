package com.example.proyectochadfrontend.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.sp
import com.example.proyectochadfrontend.R
import com.example.proyectochadfrontend.data.ReparacionResponse
import com.example.proyectochadfrontend.data.RetrofitClient
import com.example.proyectochadfrontend.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun DetalleReparacionClienteScreen(
    reparacionId: Long,
    token: String,
    onVolver: () -> Unit
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
                    reparacion = response.body()
                } else {
                    error = "Error al obtener la reparación"
                }
            } catch (e: Exception) {
                error = "Error de red: ${e.message}"
            }
        }
    }

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
            // Botón de retorno
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
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (error != null) {
                Text("Error: $error", color = cyberpunkYellow, fontFamily = Rajdhani, fontWeight = FontWeight.Bold)
            } else if (reparacion != null) {
                val rep = reparacion!!
                Text("Equipo: ${rep.tipoEquipo}", color = Color.White, fontFamily = Rajdhani, fontWeight = FontWeight.Bold)
                Text("Marca: ${rep.marca}", color = Color.White, fontFamily = Rajdhani, fontWeight = FontWeight.Bold)
                Text("Modelo: ${rep.modelo}", color = Color.White, fontFamily = Rajdhani, fontWeight = FontWeight.Bold)
                Text("Descripción de la Falla: ${rep.descripcionFalla}", color = cyberpunkYellow, fontFamily = Rajdhani, fontWeight = FontWeight.Bold)
                Text("Estado: ${rep.estado}", color = cyberpunkCyan, fontFamily = Rajdhani, fontWeight = FontWeight.Bold)
                Text("Fecha Ingreso: ${rep.fechaIngreso}", color = Color.White, fontFamily = Rajdhani, fontWeight = FontWeight.Bold)
                rep.diagnostico?.let {
                    Text("Diagnóstico: $it", color = cyberpunkPink, fontFamily = Rajdhani, fontWeight = FontWeight.Bold)
                }
                rep.solucion?.let {
                    Text("Solución: $it", color = cyberpunkYellow, fontFamily = Rajdhani, fontWeight = FontWeight.Bold)
                }
                rep.costo?.let {
                    Text("Costo: $it USD", color = cyberpunkCyan, fontFamily = Rajdhani, fontWeight = FontWeight.Bold)
                }
                rep.tecnico?.let {
                    Text("Técnico Asignado: ${it.primerNombre}", color = Color.White, fontFamily = Rajdhani, fontWeight = FontWeight.Bold)
                }
                rep.servicio?.let {
                    Text("Servicio: ${it.nombre} - ${it.descripcion}", color = Color.White, fontFamily = Rajdhani, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = cyberpunkCyan)
            ) {
                Text("Volver", color = Color.Black, fontFamily = Rajdhani, fontWeight = FontWeight.Bold)
            }
        }
    }
}
