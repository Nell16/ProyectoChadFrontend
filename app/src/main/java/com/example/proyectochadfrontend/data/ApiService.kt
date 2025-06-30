package com.example.proyectochadfrontend.data

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- AUTENTICACIÓN ---
    @POST("api/usuarios/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    // --- USUARIOS ---
    @GET("api/usuarios/por-rol")
    suspend fun getTecnicos(
        @Query("rol") rol: String = "TECNICO"
    ): Response<List<UsuarioDTO>>

    // --- REPARACIONES (Clientes) ---
    @POST("api/reparaciones")
    suspend fun crearReparacion(@Body request: ReparacionRequest): Response<Void>

    @GET("api/reparaciones/usuario/{usuarioId}")
    suspend fun getReparacionesPorUsuario(
        @Path("usuarioId") usuarioId: Long
    ): Response<List<ReparacionResponse>>

    // --- REPARACIONES (Técnicos) ---
    @GET("api/reparaciones/tecnico/{tecnicoId}")
    suspend fun getReparacionesPorTecnico(
        @Path("tecnicoId") tecnicoId: Long
    ): Response<List<ReparacionResponse>>

    @GET("api/reparaciones/sin-tecnico")
    suspend fun getReparacionesSinTecnico(): Response<List<ReparacionResponse>>

    @PUT("api/reparaciones/{reparacionId}/autoasignar")
    suspend fun autoasignarTecnico(
        @Path("reparacionId") reparacionId: Long
    ): Response<ReparacionResponse>

    @PUT("reparaciones/{id}/diagnostico")
    suspend fun actualizarDiagnostico(
        @Path("id") id: Long,
        @Body diagnostico: ReparacionDiagnosticoRequest
    ): Response<Void>

    @PUT("api/reparaciones/{reparacionId}/asignar-tecnico")
    suspend fun asignarTecnico(
        @Path("reparacionId") reparacionId: Long,
        @Query("tecnicoId") tecnicoId: Long
    ): Response<Void>

    // --- REPARACIONES (Común) ---
    @GET("api/reparaciones/{id}")
    suspend fun getReparacionPorId(@Path("id") id: Long): Response<ReparacionResponse>

    @PUT("api/reparaciones/{id}/asignar-servicio")
    suspend fun asignarServicio(
        @Path("id") reparacionId: Long,
        @Body body: AsignarServicioDTO
    ): Response<ReparacionResponse>

    // --- COMPONENTES ---
    @GET("componentes/reparacion/{id}")
    suspend fun getComponentesPorReparacion(@Path("id") id: Long): Response<List<ComponenteDTO>>

    // --- SERVICIOS ---
    @GET("api/servicios")
    suspend fun getServicios(): Response<List<ServicioDTO>>

    @POST("api/servicios")
    suspend fun crearServicio(@Body servicio: ServicioRequest): Response<ServicioDTO>

    @DELETE("api/servicios/{id}")
    suspend fun eliminarServicio(@Path("id") id: Long): Response<Void>

    @PUT("api/servicios/{id}")
    suspend fun actualizarServicio(
        @Path("id") id: Long,
        @Body servicio: ServicioRequest
    ): Response<ServicioDTO>

    // NOTA: Agrega aquí futuras funciones según avance el proyecto.
}
