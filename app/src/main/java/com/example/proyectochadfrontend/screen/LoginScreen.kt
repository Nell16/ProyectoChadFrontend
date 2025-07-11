package com.example.proyectochadfrontend.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectochadfrontend.model.*
import com.example.proyectochadfrontend.network.RetrofitClient
import com.example.proyectochadfrontend.ui.components.*
import com.example.proyectochadfrontend.ui.theme.ProyectoChadFrontendTheme
import com.example.proyectochadfrontend.util.UserPreferences
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (LoginResponse) -> Unit
) {
    ProyectoChadFrontendTheme(useCyberpunk = true) {
        val context = LocalContext.current
        val userPreferences = remember(context) { UserPreferences(context) }
        val scope = rememberCoroutineScope()

        var isLoginMode by remember { mutableStateOf(true) }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var primerNombre by remember { mutableStateOf("") }
        var primerApellido by remember { mutableStateOf("") }
        var telefono by remember { mutableStateOf("") }
        var registroExitoso by remember { mutableStateOf(false) }
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

                AnimatedContent(targetState = isLoginMode, label = "mode-switch") { isLogin ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (!isLogin) {
                            CyberpunkTextField(
                                value = primerNombre,
                                onValueChange = { primerNombre = it },
                                label = "Nombre",
                                textStyle = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            CyberpunkTextField(
                                value = primerApellido,
                                onValueChange = { primerApellido = it },
                                label = "Apellido",
                                textStyle = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            CyberpunkTextField(
                                value = telefono,
                                onValueChange = { telefono = it },
                                label = "Teléfono",
                                textStyle = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        CyberpunkTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Correo electrónico",
                            textStyle = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        CyberpunkTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Contraseña",
                            isPassword = true,
                            textStyle = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        CyberpunkButton(
                            text = if (isLoading) if (isLogin) "Iniciando..." else "Registrando..." else if (isLogin) "Iniciar sesión" else "Registrarse",
                            onClick = {
                                if (email.isNotBlank() && password.isNotBlank()) {
                                    isLoading = true
                                    errorMsg = null
                                    scope.launch {
                                        try {
                                            if (isLogin) {
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
                                                        errorMsg = "Error inesperado."
                                                    }
                                                } else {
                                                    errorMsg = "Credenciales incorrectas."
                                                }
                                            } else {
                                                if (primerNombre.isBlank() || primerApellido.isBlank() || telefono.isBlank()) {
                                                    errorMsg = "Todos los campos son obligatorios."
                                                } else {
                                                    val newUser = UsuarioRequest(
                                                        primerNombre = primerNombre,
                                                        segundoNombre = "",
                                                        primerApellido = primerApellido,
                                                        segundoApellido = "",
                                                        correo = email,
                                                        contrasena = password,
                                                        telefono = telefono,
                                                        fotoPerfilUrl = ""
                                                    )
                                                    val response = RetrofitClient.apiServiceNoAuth.registrarUsuario(newUser)
                                                    if (response.isSuccessful) {
                                                        registroExitoso = true
                                                        isLoginMode = true
                                                        primerNombre = ""
                                                        primerApellido = ""
                                                        telefono = ""
                                                        password = ""
                                                    } else {
                                                        errorMsg = "El correo ya está en uso o datos inválidos."
                                                    }
                                                }
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

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(color = Color.White, fontWeight = FontWeight.Bold)) {
                                    append(if (isLogin) "¿No tienes cuenta? " else "¿Ya tienes una cuenta? ")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = Color.Cyan,
                                        fontWeight = FontWeight.Bold,
                                        textDecoration = TextDecoration.Underline
                                    )
                                ) {
                                    append(if (isLogin) "Registrarse" else "Iniciar sesión")
                                }
                            },
                            modifier = Modifier.clickable { isLoginMode = !isLoginMode }
                        )
                    }
                }

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = errorMsg ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (isLoading) {
                LoadingHackEffect()
            }

            if (registroExitoso) {
                AlertDialog(
                    onDismissRequest = { registroExitoso = false },
                    title = { Text("¡Registro exitoso!", fontWeight = FontWeight.Bold) },
                    text = { Text("Tu cuenta ha sido creada correctamente.") },
                    confirmButton = {
                        CyberpunkButton(
                            text = "Iniciar sesión",
                            onClick = { registroExitoso = false }
                        )
                    }
                )
            }
        }
    }
}
