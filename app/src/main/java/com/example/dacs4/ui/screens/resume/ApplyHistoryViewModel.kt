package com.example.dacs4.ui.screens.resume

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs4.data.model.response.ResumeResponse
import com.example.dacs4.data.repository.ResumeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.dacs4.core.socket.SocketManager
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

data class ApplyHistoryUiState(
    val isLoading: Boolean = false,
    val resumes: List<ResumeResponse> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class ApplyHistoryViewModel @Inject constructor(
    private val resumeRepository: ResumeRepository,
    private val socketManager: SocketManager
) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val _uiState = MutableStateFlow(ApplyHistoryUiState(isLoading = true))
    val uiState: StateFlow<ApplyHistoryUiState> = _uiState

    init {
        loadHistory()
        setupSocketSubscription()
    }

    private fun setupSocketSubscription() {
        // Lắng nghe thay đổi trạng thái CV từ admin để cập nhật ngầm
        disposables.add(socketManager.subscribe("/topic/resumes") {
            loadHistory(showLoading = false)
        })
    }

    fun loadHistory(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) {
                _uiState.update { it.copy(isLoading = true, error = null) }
            }
            resumeRepository.getMyResumes()
                .onSuccess { paginationData ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            resumes = paginationData.result,
                            error = null
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Có lỗi xảy ra"
                        )
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
