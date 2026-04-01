package com.example.dacs4.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs4.data.model.request.LoginRequest
import com.example.dacs4.data.model.response.AuthResponse
import com.example.dacs4.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Trạng thái của màn hình (Mô phỏng Redux Store)
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val authData: AuthResponse) : AuthState()
    data class Error(val message: String) : AuthState()
}

/**
 * BỘ NÃO CỦA MÀN HÌNH ĐĂNG NHẬP
 * Vai trò tương đương redux/slice/accountSlice.ts của Web.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Dòng suối dữ liệu để UI hứng trạng thái mới nhất
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _authState.value = AuthState.Error("Vui lòng điền đủ Email và Mật khẩu")
            return
        }

        // Báo cho UI hiện vòng xoay quay mòng mòng
        _authState.value = AuthState.Loading

        // Kéo thêm một sợi dây riêng (Coroutine) để chạy ngầm gọi API login
        viewModelScope.launch {
            val result = authRepository.login(LoginRequest(email, pass))
            
            result.onSuccess { data ->
                _authState.value = AuthState.Success(data)
            }.onFailure { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Lỗi củ chuối không hẹn trước")
            }
        }
    }

    // Reset lại trạng thái lỗi mỗi khi người dùng ấn vào Cục thông báo
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
