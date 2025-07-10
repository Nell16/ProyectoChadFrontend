package com.example.proyectochadfrontend.data

data class LoginResponse(
    val token: String,
    val idUsuario: Long,
    val correo: String,
    val rol: String,
    val primerNombre: String,
    val segundoNombre: String?,
    val primerApellido: String,
    val segundoApellido: String?,
    val telefono: String,
    val fotoPerfilUrl: String?
)

