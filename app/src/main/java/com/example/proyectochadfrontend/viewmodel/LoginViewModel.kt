package com.example.proyectochadfrontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectochadfrontend.repository.LoginRepository
import com.example.proyectochadfrontend.model.LoginRequest
import com.example.proyectochadfrontend.model.LoginResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val response: LoginResponse) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel(private val repository: LoginRepository = LoginRepository()) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(username: String, password: String) {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            try {
                val response: Response<LoginResponse> =
                    repository.login(LoginRequest(username, password))

                if (response.isSuccessful && response.body() != null) {
                    _loginState.value = LoginState.Success(response.body()!!)
                } else {
                    _loginState.value = LoginState.Error("Credenciales inválidas o error ${response.code()}")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Error de red: ${e.message}")
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}
