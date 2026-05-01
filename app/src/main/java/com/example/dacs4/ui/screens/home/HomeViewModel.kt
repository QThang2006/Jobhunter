package com.example.dacs4.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs4.core.security.TokenManager
import com.example.dacs4.data.model.response.CompanyResponse
import com.example.dacs4.data.model.response.JobResponse
import com.example.dacs4.data.repository.CompanyRepository
import com.example.dacs4.data.repository.JobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(
        val jobs: List<JobResponse>,
        val companies: List<CompanyResponse>
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val companyRepository: CompanyRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        fetchHomeData()
    }

    fun fetchHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            // Gọi song song 2 API (async) để tối ưu thời gian chờ
            val jobsDeferred = async { jobRepository.getJobs(1, 10) }
            val companiesDeferred = async { companyRepository.getCompanies(1, 10) }

            val jobsResult = jobsDeferred.await()
            val companiesResult = companiesDeferred.await()

            if (jobsResult.isSuccess && companiesResult.isSuccess) {
                _uiState.value = HomeUiState.Success(
                    jobs = jobsResult.getOrDefault(null)?.result ?: emptyList(),
                    companies = companiesResult.getOrDefault(null)?.result ?: emptyList()
                )
            } else {
                val error = jobsResult.exceptionOrNull()?.message
                    ?: companiesResult.exceptionOrNull()?.message
                    ?: "Lỗi không xác định"
                _uiState.value = HomeUiState.Error(error)
            }
        }
    }

    fun logout() {
        tokenManager.clearToken()
    }
}
