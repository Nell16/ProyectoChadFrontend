package com.example.proyectochadfrontend.model

data class ReparacionResponse(
    val id: Long,
    val usuario: UsuarioDTO,
    val tecnico: UsuarioDTO?,
    val tipoEquipo: String,
    val marca: String,
    val modelo: String,
    val descripcionFalla: String,
    val fechaIngreso: String,
    val estado: String,
    val diagnostico: String?,
    val solucion: String?,
    val costo: Double?,
    val servicio: ServicioDTO?,
    val componentes: List<ComponenteDTO>
)