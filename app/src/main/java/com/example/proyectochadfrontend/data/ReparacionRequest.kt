package com.example.proyectochadfrontend.data

data class ReparacionRequest(
    val usuarioId: Long,
    val tipoEquipo: String,
    val marca: String,
    val modelo: String,
    val descripcionFalla: String
)
