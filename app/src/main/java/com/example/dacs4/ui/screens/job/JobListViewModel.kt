package com.example.dacs4.ui.screens.job

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs4.data.model.response.JobResponse
import com.example.dacs4.data.repository.JobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JobListUiState(
    val isLoading: Boolean = false,
    val isFetchingNextPage: Boolean = false,
    val jobs: List<JobResponse> = emptyList(),
    val error: String? = null,
    val page: Int = 1,
    val totalPages: Int = 1,
    val hasMoreData: Boolean = true,
    
    // Filters
    val searchQuery: String = "",
    val locationFilter: String = "",
    val skillsFilter: String = ""
)

@HiltViewModel
class JobListViewModel @Inject constructor(
    private val jobRepository: JobRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(JobListUiState())
    val uiState: StateFlow<JobListUiState> = _uiState.asStateFlow()

    init {
        fetchJobs(isRefresh = true)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun applyFilters(location: String, skills: String) {
        _uiState.update { it.copy(locationFilter = location, skillsFilter = skills) }
        fetchJobs(isRefresh = true)
    }

    fun performSearch() {
        fetchJobs(isRefresh = true)
    }

    fun fetchNextPage() {
        val state = _uiState.value
        if (state.hasMoreData && !state.isLoading && !state.isFetchingNextPage) {
            fetchJobs(isRefresh = false)
        }
    }

    fun refresh() {
        fetchJobs(isRefresh = true)
    }

    private fun fetchJobs(isRefresh: Boolean) {
        viewModelScope.launch {
            val currentState = _uiState.value
            
            if (isRefresh) {
                _uiState.update { it.copy(isLoading = true, page = 1, error = null) }
            } else {
                _uiState.update { it.copy(isFetchingNextPage = true, error = null) }
            }

            val targetPage = if (isRefresh) 1 else currentState.page + 1
            
            val parts = mutableListOf<String>()
            if (currentState.searchQuery.isNotBlank()) {
                parts.add("name~'%${currentState.searchQuery}%'")
            }
            if (currentState.locationFilter.isNotBlank() && currentState.locationFilter != "ALL") {
                parts.add("location='${currentState.locationFilter}'")
            }
            if (currentState.skillsFilter.isNotBlank()) {
                parts.add("skills.name~'%${currentState.skillsFilter}%'")
            }
            
            val filterString = if (parts.isNotEmpty()) parts.joinAnd(" and ") else null

            val result = jobRepository.getJobs(page = targetPage, pageSize = 10, filter = filterString)

            result.onSuccess { paginationData ->
                val newJobs = paginationData.result
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        isFetchingNextPage = false,
                        jobs = if (isRefresh) newJobs else state.jobs + newJobs,
                        page = targetPage,
                        totalPages = if (paginationData.meta.pages == 0) 1 else paginationData.meta.pages,
                        hasMoreData = targetPage < paginationData.meta.pages
                    )
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isFetchingNextPage = false,
                        error = e.message ?: "Đã xảy ra lỗi kết nối"
                    )
                }
            }
        }
    }

    private fun List<String>.joinAnd(separator: String): String {
        return this.joinToString(separator)
    }
}
