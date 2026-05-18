package com.example.dacs4.ui.screens.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs4.data.model.response.CompanyResponse
import com.example.dacs4.data.model.response.JobResponse
import com.example.dacs4.data.repository.CompanyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CompanyDetailUiState(
    val isLoading: Boolean = false,
    val company: CompanyResponse? = null,
    val jobCount: Long = 0,
    val jobs: List<JobResponse> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class CompanyViewModel @Inject constructor(
    private val companyRepository: CompanyRepository,
    private val jobRepository: com.example.dacs4.data.repository.JobRepository,
    private val socketManager: com.example.dacs4.core.socket.SocketManager
) : ViewModel() {
 
    private val disposables = io.reactivex.disposables.CompositeDisposable()
    private var currentCompanyId: String? = null
 
    private val _uiState = MutableStateFlow(CompanyDetailUiState(isLoading = true))
    val uiState: StateFlow<CompanyDetailUiState> = _uiState
 
    private fun setupSocketSubscription() {
        disposables.add(socketManager.subscribe("/topic/companies") {
            currentCompanyId?.let { fetchCompanyDetail(it, showLoading = false) }
        })
        disposables.add(socketManager.subscribe("/topic/jobs") {
            currentCompanyId?.let { fetchCompanyDetail(it, showLoading = false) }
        })
    }

    fun fetchCompanyDetail(companyId: String, showLoading: Boolean = true) {
        currentCompanyId = companyId
        
        if (disposables.size() == 0) {
            setupSocketSubscription()
        }
 
        viewModelScope.launch {
            if (showLoading) {
                _uiState.update { it.copy(isLoading = true, error = null) }
            }
            
            // Parallel fetch company detail and job count
            val companyResult = companyRepository.getCompanyById(companyId)
            val jobsResult = jobRepository.getJobs(page = 1, pageSize = 20, filter = "company.id : '$companyId' and active : true")

            companyResult.onSuccess { company ->
                val jobsData = jobsResult.getOrNull()
                val count = jobsData?.meta?.total ?: 0
                val jobsList = jobsData?.result ?: emptyList()
                
                _uiState.update { it.copy(
                    isLoading = false, 
                    company = company, 
                    jobCount = count,
                    jobs = jobsList,
                    error = null
                ) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message ?: "Lỗi tải chi tiết công ty") }
            }
        }
    }
 
    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
