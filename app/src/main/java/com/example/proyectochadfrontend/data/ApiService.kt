package com.example.proyectochadfrontend.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("api/usuarios/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @GET("api/reparaciones/usuario/{usuarioId}")
    suspend fun getReparacionesPorUsuario(
        @Path("usuarioId") usuarioId: Long
    ): Response<List<ReparacionResponse>>

    @POST("api/reparaciones")
    suspend fun crearReparacion(@Body request: ReparacionRequest): Response<Void>

    @PUT("reparaciones/{id}/diagnostico")
    suspend fun actualizarDiagnostico(
        @Path("id") id: Long,
        @Body diagnostico: ReparacionDiagnosticoRequest
    ): Response<Void>

    @GET("componentes/reparacion/{id}")
    suspend fun getComponentesPorReparacion(@Path("id") id: Long): Response<List<ComponenteDTO>>

    @GET("reparaciones/sin-tecnico")
    suspend fun getReparacionesSinTecnico(): Response<List<ReparacionResponse>>

    @GET("usuarios/tecnicos")
    suspend fun getTecnicos(): Response<List<UsuarioDTO>>

    @PUT("reparaciones/{id}/asignar-tecnico/{tecnicoId}")
    suspend fun asignarTecnico(
        @Path("id") reparacionId: Long,
        @Path("tecnicoId") tecnicoId: Long
    ): Response<Void>

    @PUT("api/reparaciones/{reparacionId}/autoasignar")
    suspend fun autoasignarTecnico(
        @Path("reparacionId") reparacionId: Long
    ): Response<ReparacionResponse>


    // Aquí luego se añadirán otras funciones: obtener reparaciones, registrar servicios, etc.
}
