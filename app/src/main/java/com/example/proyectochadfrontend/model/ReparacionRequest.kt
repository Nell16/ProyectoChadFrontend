package com.example.proyectochadfrontend.model

data class ReparacionRequest(
    val usuarioId: Long,
    val tipoEquipo: String,
    val marca: String,
    val modelo: String,
    val descripcionFalla: String
)
