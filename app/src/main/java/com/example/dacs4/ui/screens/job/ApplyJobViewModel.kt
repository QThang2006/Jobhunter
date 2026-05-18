package com.example.dacs4.ui.screens.job

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs4.core.security.TokenManager
import com.example.dacs4.data.repository.ResumeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ApplyUiState {
    object Idle : ApplyUiState()
    data class FileSelected(val uri: Uri, val fileName: String) : ApplyUiState()
    object Uploading : ApplyUiState()
    object Submitting : ApplyUiState()
    object Success : ApplyUiState()
    data class Error(val message: String) : ApplyUiState()
}
@HiltViewModel
class ApplyJobViewModel @Inject constructor(
    private val resumeRepository: ResumeRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    val userEmail: String = tokenManager.getUserEmail() ?: ""

    private val _uiState = MutableStateFlow<ApplyUiState>(ApplyUiState.Idle)
    val uiState: StateFlow<ApplyUiState> = _uiState

    fun onFileSelected(uri: Uri, context: Context) {
        val fileName = getFileName(uri, context)
        _uiState.value = ApplyUiState.FileSelected(uri, fileName)
    }

    fun submit(jobId: Long, context: Context) {
        val currentState = _uiState.value
        if (currentState !is ApplyUiState.FileSelected) return

        viewModelScope.launch {
            // Bước 1: Upload file
            _uiState.value = ApplyUiState.Uploading
            val uploadResult = resumeRepository.uploadCv(currentState.uri, context)
            if (uploadResult.isFailure) {
                _uiState.value = ApplyUiState.Error(
                    uploadResult.exceptionOrNull()?.message ?: "Upload thất bại"
                )
                return@launch
            }

            // Bước 2: Nộp đơn ứng tuyển
            _uiState.value = ApplyUiState.Submitting
            val cvFileName = uploadResult.getOrThrow()
            val applyResult = resumeRepository.applyJob(jobId, cvFileName)
            if (applyResult.isSuccess) {
                _uiState.value = ApplyUiState.Success
            } else {
                _uiState.value = ApplyUiState.Error(
                    applyResult.exceptionOrNull()?.message ?: "Ứng tuyển thất bại"
                )
            }
        }
    }

    fun reset() {
        _uiState.value = ApplyUiState.Idle
    }

    private fun getFileName(uri: Uri, context: Context): String {
        var name = "cv_${System.currentTimeMillis()}.pdf"
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val idx = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && idx >= 0) name = cursor.getString(idx)
        }
        return name
    }
}
