package com.example.proyectochadfrontend.screen

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectochadfrontend.data.LoginRequest
import com.example.proyectochadfrontend.data.LoginResponse
import com.example.proyectochadfrontend.data.RetrofitClient
import com.example.proyectochadfrontend.util.UserPreferences
import kotlinx.coroutines.*

@Composable
fun LoginScreen(
    onLoginSuccess: (LoginResponse) -> Unit
) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Iniciar sesión", fontSize = 28.sp, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                isLoading = true
                errorMsg = null

                scope.launch {
                    try {
                        val api = RetrofitClient.getClient("")
                        val response = api.login(LoginRequest(correo = email, contrasena = password))

                        if (response.isSuccessful) {
                            val body = response.body()
                            if (body != null) {
                                // Guardar datos del usuario en DataStore
                                userPreferences.saveUserData(
                                    token = body.token,
                                    rol = body.rol,
                                    correo = body.correo,
                                    idUsuario = body.idUsuario
                                )
                                onLoginSuccess(body)
                            } else {
                                errorMsg = "Respuesta vacía del servidor"
                            }
                        } else {
                            errorMsg = "Credenciales incorrectas"
                        }

                    } catch (e: Exception) {
                        errorMsg = "Error de conexión: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Ingresar")
        }

        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        errorMsg?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
    }
}
