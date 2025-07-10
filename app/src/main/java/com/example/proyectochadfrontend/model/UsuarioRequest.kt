// data/UsuarioRequest.kt
package com.example.proyectochadfrontend.model

data class UsuarioRequest(
    val primerNombre: String,
    val segundoNombre: String,
    val primerApellido: String,
    val segundoApellido: String,
    val correo: String,
    val contrasena: String,
    val telefono: String,
    val fotoPerfilUrl: String = "",
    val rol: String = "CLIENTE"
)
