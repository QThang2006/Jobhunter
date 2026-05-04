package com.example.dacs4.ui.screens.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs4.data.model.response.CompanyResponse
import com.example.dacs4.data.repository.CompanyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CompanyDetailUiState {
    object Loading : CompanyDetailUiState()
    data class Success(val company: CompanyResponse) : CompanyDetailUiState()
    data class Error(val message: String) : CompanyDetailUiState()
}

@HiltViewModel
class CompanyViewModel @Inject constructor(
    private val companyRepository: CompanyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CompanyDetailUiState>(CompanyDetailUiState.Loading)
    val uiState: StateFlow<CompanyDetailUiState> = _uiState

    fun fetchCompanyDetail(companyId: String) {
        viewModelScope.launch {
            _uiState.value = CompanyDetailUiState.Loading
            companyRepository.getCompanyById(companyId).onSuccess { company ->
                _uiState.value = CompanyDetailUiState.Success(company)
            }.onFailure { error ->
                _uiState.value = CompanyDetailUiState.Error(error.message ?: "Lỗi tải chi tiết công ty")
            }
        }
    }
}
