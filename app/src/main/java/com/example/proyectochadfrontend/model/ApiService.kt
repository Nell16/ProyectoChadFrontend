package com.example.proyectochadfrontend.model

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- AUTENTICACIÓN ---
    @POST("api/usuarios/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    // --- USUARIOS ---

    @POST("api/usuarios/registro")
    suspend fun registrarUsuario(
        @Body usuario: UsuarioRequest
    ): Response<UsuarioDTO>


    @GET("api/usuarios/por-rol")
    suspend fun getTecnicos(
        @Query("rol") rol: String = "TECNICO"
    ): Response<List<UsuarioDTO>>

    @GET("api/usuarios")
    suspend fun getTodosLosUsuarios(): Response<List<UsuarioDTO>>


    // --- REPARACIONES (Clientes) ---
    @POST("api/reparaciones")
    suspend fun crearReparacion(@Body request: ReparacionRequest): Response<Void>

    @GET("api/reparaciones/usuario/{usuarioId}")
    suspend fun getReparacionesPorUsuario(
        @Path("usuarioId") usuarioId: Long
    ): Response<List<ReparacionResponse>>

    @GET("api/reparaciones")
    suspend fun obtenerTodasLasReparaciones(): Response<List<ReparacionResponse>>


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

    @GET("api/componentes/reparacion/{reparacionId}")
    suspend fun getComponentesPorReparacion(
        @Path("reparacionId") reparacionId: Long
    ): Response<List<ComponenteDTO>>

    @GET("api/componentes")
    suspend fun getTodosLosComponentes(): Response<List<ComponenteDTO>>

    @POST("api/componentes")
    suspend fun agregarComponente(
        @Body componente: ComponenteRequestDTO
    ): Response<Void>

    @PUT("api/componentes/{id}/general")
    suspend fun actualizarComponenteGeneral(
        @Path("id") id: Long,
        @Body componente: ComponenteGeneralDTO
    ): Response<ComponenteDTO>

    @DELETE("api/componentes/{id}")
    suspend fun eliminarComponente(
        @Path("id") id: Long
    ): Response<Void>

    @POST("api/componentes/generales")
    suspend fun crearComponenteGeneral(@Body componente: ComponenteGeneralDTO): Response<ComponenteDTO>

    @POST("api/componentes/asignar-general")
    suspend fun asignarComponenteGeneralAReparacion(
        @Query("componenteId") componenteId: Long,
        @Query("reparacionId") reparacionId: Long
    ): Response<ComponenteDTO>


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


    // --- FOTOS PERFIL ---
    @Multipart
    @POST("api/usuarios/{id}/foto")
    suspend fun subirFotoPerfil(
        @Path("id") userId: Long,
        @Part archivo: MultipartBody.Part
    ): Response<String>

    @PUT("api/usuarios/{id}")
    suspend fun actualizarPerfil(
        @Path("id") id: Long,
        @Body usuario: UsuarioDTO
    ): Response<UsuarioDTO>

    @PUT("api/usuarios/{id}/rol")
    suspend fun actualizarRolUsuario(
        @Path("id") id: Long,
        @Query("nuevoRol") nuevoRol: String
    ): Response<UsuarioDTO>



    // NOTA: Agrega aquí futuras funciones según avance el proyecto.
}
