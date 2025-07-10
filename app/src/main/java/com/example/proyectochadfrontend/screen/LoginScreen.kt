package com.example.proyectochadfrontend.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectochadfrontend.data.LoginRequest
import com.example.proyectochadfrontend.data.LoginResponse
import com.example.proyectochadfrontend.data.RetrofitClient
import com.example.proyectochadfrontend.ui.components.*
import com.example.proyectochadfrontend.ui.theme.ProyectoChadFrontendTheme
import com.example.proyectochadfrontend.util.UserPreferences
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: (LoginResponse) -> Unit
) {
    ProyectoChadFrontendTheme(useCyberpunk = true) {
        val context = LocalContext.current
        val userPreferences = remember(context) { UserPreferences(context) }
        val scope = rememberCoroutineScope()

        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var errorMsg by remember { mutableStateOf<String?>(null) }
        var isLoading by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxSize()) {
            CyberpunkBackground()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GlitchText(
                    text = "Gestion tecnica sigma",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(32.dp))

                CyberpunkTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Correo electrónico",
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                CyberpunkTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Contraseña",
                    isPassword = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))

                CyberpunkButton(
                    text = if (isLoading) "Iniciando..." else "Iniciar sesión",
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            isLoading = true
                            errorMsg = null
                            scope.launch {
                                try {
                                    val request = LoginRequest(correo = email, contrasena = password)
                                    val response = RetrofitClient.apiServiceNoAuth.login(request)

                                    if (response.isSuccessful) {
                                        val body = response.body()
                                        if (body != null) {
                                            userPreferences.saveUserData(
                                                token = body.token,
                                                rol = body.rol,
                                                correo = body.correo,
                                                idUsuario = body.idUsuario
                                            )
                                            onLoginSuccess(body)
                                        } else {
                                            errorMsg = "Error inesperado. Respuesta vacía."
                                        }
                                    } else {
                                        errorMsg = "Error: ${response.code()} - ${response.message()}"
                                    }
                                } catch (e: Exception) {
                                    errorMsg = "Error de red: ${e.message}"
                                } finally {
                                    isLoading = false
                                }
                            }
                        } else {
                            errorMsg = "Todos los campos son obligatorios."
                        }
                    }
                )

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = errorMsg ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold // ✅ mensaje de error en bold
                    )
                }
            }

            if (isLoading) {
                LoadingHackEffect()
            }
        }
    }
}
