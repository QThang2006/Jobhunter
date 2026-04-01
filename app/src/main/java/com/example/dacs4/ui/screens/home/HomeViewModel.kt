package com.example.dacs4.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs4.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * QUẢN LÝ LOGIC TRANG CHỦ
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun logout() {
        viewModelScope.launch {
            // Gọi Repo xóa token trong "két sắt" và gọi API logout (nếu có mạng)
            authRepository.logout()
        }
    }
}
