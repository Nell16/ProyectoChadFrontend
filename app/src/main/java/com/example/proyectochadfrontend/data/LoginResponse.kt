package com.example.proyectochadfrontend.data

data class LoginResponse(
    val token: String,
    val correo: String,
    val rol: String,
    val idUsuario: Long
)
