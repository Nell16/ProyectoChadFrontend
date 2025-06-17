package com.example.proyectochadfrontend.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("api/usuarios/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @GET("api/reparaciones/usuario/{usuarioId}")
    suspend fun getReparacionesPorUsuario(
        @Path("usuarioId") usuarioId: Long
    ): Response<List<ReparacionResponse>>

    // Aquí luego se añadirán otras funciones: obtener reparaciones, registrar servicios, etc.
}
