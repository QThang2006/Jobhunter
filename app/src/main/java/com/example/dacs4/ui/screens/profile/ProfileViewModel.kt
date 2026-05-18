package com.example.dacs4.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs4.core.security.TokenManager
import com.example.dacs4.data.model.request.UpdateUserRequest
import com.example.dacs4.data.model.response.UserDetailResponse
import com.example.dacs4.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val user: UserDetailResponse) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState

    private val _updateState = MutableStateFlow<String?>(null)
    val updateState: StateFlow<String?> = _updateState

    private val _changePasswordState = MutableStateFlow<String?>(null)
    val changePasswordState: StateFlow<String?> = _changePasswordState

    init {
        loadProfile()
    }

    fun loadProfile() {
        _uiState.value = ProfileUiState.Loading
        viewModelScope.launch {
            userRepository.getCurrentUser()
                .onSuccess { _uiState.value = ProfileUiState.Success(it) }
                .onFailure { _uiState.value = ProfileUiState.Error(it.message ?: "Lỗi không xác định") }
        }
    }

    fun updateUser(name: String, age: Int, gender: String, address: String) {
        val currentUser = (_uiState.value as? ProfileUiState.Success)?.user ?: return
        viewModelScope.launch {
            val request = UpdateUserRequest(
                id = currentUser.id,
                name = name,
                age = age,
                gender = gender,
                address = address,
                email = currentUser.email
            )
            userRepository.updateUser(request)
                .onSuccess {
                    _uiState.value = ProfileUiState.Success(it)
                    _updateState.value = "Cập nhật thông tin thành công!"
                }
                .onFailure { _updateState.value = it.message }
        }
    }

    fun changePassword(oldPass: String, newPass: String) {
        viewModelScope.launch {
            userRepository.changePassword(oldPass, newPass)
                .onSuccess { _changePasswordState.value = "✅ Đổi mật khẩu thành công!" }
                .onFailure { _changePasswordState.value = it.message }
        }
    }

    fun consumeUpdateState() { _updateState.value = null }
    fun consumePasswordState() { _changePasswordState.value = null }

    fun getUserName(): String = tokenManager.getUserName() ?: "Người dùng"
    fun getUserEmail(): String = tokenManager.getUserEmail() ?: ""
}
