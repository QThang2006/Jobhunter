package com.example.dacs4.ui.screens.job

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs4.data.model.response.JobResponse
import com.example.dacs4.data.repository.JobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class JobDetailUiState {
    object Loading : JobDetailUiState()
    data class Success(val job: JobResponse) : JobDetailUiState()
    data class Error(val message: String) : JobDetailUiState()
}

@HiltViewModel
class JobDetailViewModel @Inject constructor(
    private val jobRepository: JobRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<JobDetailUiState>(JobDetailUiState.Loading)
    val uiState: StateFlow<JobDetailUiState> = _uiState

    fun fetchJobDetail(jobId: String) {
        viewModelScope.launch {
            _uiState.value = JobDetailUiState.Loading
            jobRepository.getJobById(jobId).onSuccess { job ->
                _uiState.value = JobDetailUiState.Success(job)
            }.onFailure { error ->
                _uiState.value = JobDetailUiState.Error(error.message ?: "Lỗi tải chi tiết công việc")
            }
        }
    }
}
