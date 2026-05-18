package com.example.dacs4.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs4.data.model.request.RegisterRequest
import com.example.dacs4.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    object Success : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun register(name: String, email: String, pass: String) {
        _uiState.value = RegisterUiState.Loading
        viewModelScope.launch {
            val request = RegisterRequest(
                name = name,
                email = email,
                password = pass,
                age = 18, // Default values matching API requirement
                gender = "MALE",
                address = ""
            )
            authRepository.register(request)
                .onSuccess {
                    _uiState.value = RegisterUiState.Success
                }
                .onFailure {
                    _uiState.value = RegisterUiState.Error(it.message ?: "Đăng ký thất bại")
                }
        }
    }

    fun resetState() {
        _uiState.value = RegisterUiState.Idle
    }
}
