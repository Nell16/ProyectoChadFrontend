package com.example.proyectochadfrontend.model

data class ReparacionDiagnosticoRequest(
    val diagnostico: String,
    val solucion: String,
    val costo: Double
)
