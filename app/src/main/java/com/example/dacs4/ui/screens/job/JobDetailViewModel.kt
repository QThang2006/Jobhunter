package com.example.dacs4.ui.screens.job

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs4.data.model.response.JobResponse
import com.example.dacs4.data.repository.JobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JobDetailUiState(
    val isLoading: Boolean = false,
    val job: JobResponse? = null,
    val error: String? = null
)

@HiltViewModel
class JobDetailViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val socketManager: com.example.dacs4.core.socket.SocketManager
) : ViewModel() {
 
    private val disposables = io.reactivex.disposables.CompositeDisposable()
    private var currentJobId: String? = null
 
    private val _uiState = MutableStateFlow(JobDetailUiState(isLoading = true))
    val uiState: StateFlow<JobDetailUiState> = _uiState
 
    private fun setupSocketSubscription() {
        disposables.add(socketManager.subscribe("/topic/jobs") {
            currentJobId?.let { fetchJobDetail(it, showLoading = false) }
        })
    }

    fun fetchJobDetail(jobId: String, showLoading: Boolean = true) {
        currentJobId = jobId
        
        // Khởi tạo socket subscription nếu chưa có
        if (disposables.size() == 0) {
            setupSocketSubscription()
        }
 
        viewModelScope.launch {
            if (showLoading) {
                _uiState.update { it.copy(isLoading = true, error = null) }
            }
            jobRepository.getJobById(jobId).onSuccess { job ->
                _uiState.update { it.copy(isLoading = false, job = job, error = null) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message ?: "Lỗi tải chi tiết công việc") }
            }
        }
    }
 
    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
