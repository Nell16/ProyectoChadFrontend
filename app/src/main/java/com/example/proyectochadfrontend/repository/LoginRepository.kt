package com.example.proyectochadfrontend.repository

import com.example.proyectochadfrontend.model.LoginRequest
import com.example.proyectochadfrontend.model.LoginResponse
import com.example.proyectochadfrontend.network.RetrofitClient
import retrofit2.Response

class LoginRepository {

    suspend fun login(request: LoginRequest): Response<LoginResponse> {
        val api = RetrofitClient.getClientSinToken()
        return api.login(request)
    }
}