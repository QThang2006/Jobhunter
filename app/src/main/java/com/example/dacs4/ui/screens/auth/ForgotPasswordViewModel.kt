package com.example.dacs4.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs4.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ForgotPasswordUiState {
    object Idle : ForgotPasswordUiState()
    object Loading : ForgotPasswordUiState()
    data class Error(val message: String) : ForgotPasswordUiState()

    // Step states
    object OtpSent : ForgotPasswordUiState()
    object OtpVerified : ForgotPasswordUiState()
    object PasswordResetSuccess : ForgotPasswordUiState()
}

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ForgotPasswordUiState>(ForgotPasswordUiState.Idle)
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState

    fun requestOtp(email: String) {
        _uiState.value = ForgotPasswordUiState.Loading
        viewModelScope.launch {
            authRepository.requestOtp(email)
                .onSuccess {
                    _uiState.value = ForgotPasswordUiState.OtpSent
                }
                .onFailure {
                    _uiState.value = ForgotPasswordUiState.Error(it.message ?: "Có lỗi xảy ra")
                }
        }
    }

    fun verifyOtp(email: String, otp: String) {
        _uiState.value = ForgotPasswordUiState.Loading
        viewModelScope.launch {
            authRepository.verifyOtp(email, otp)
                .onSuccess {
                    _uiState.value = ForgotPasswordUiState.OtpVerified
                }
                .onFailure {
                    _uiState.value = ForgotPasswordUiState.Error(it.message ?: "Có lỗi xảy ra")
                }
        }
    }

    fun resetPassword(email: String, otp: String, newPass: String) {
        _uiState.value = ForgotPasswordUiState.Loading
        viewModelScope.launch {
            authRepository.resetPassword(email, otp, newPass)
                .onSuccess {
                    _uiState.value = ForgotPasswordUiState.PasswordResetSuccess
                }
                .onFailure {
                    _uiState.value = ForgotPasswordUiState.Error(it.message ?: "Có lỗi xảy ra")
                }
        }
    }

    fun resetState() {
        _uiState.value = ForgotPasswordUiState.Idle
    }
}
