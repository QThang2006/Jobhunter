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
import com.example.dacs4.core.socket.SocketManager
import io.reactivex.disposables.CompositeDisposable
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
    val skillsFilter: String = "",
    val salaryMin: Double? = null,
    val salaryMax: Double? = null,
    val levelFilter: String = "",
    val companyId: String? = null,
    val availableSkills: List<com.example.dacs4.data.model.response.SkillResponse> = emptyList(),
    
    // UI States for sheets
    val isLocationSheetVisible: Boolean = false,
    val isSalarySheetVisible: Boolean = false,
    val isOtherSheetVisible: Boolean = false
)

@HiltViewModel
class JobListViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val socketManager: SocketManager
) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val _uiState = MutableStateFlow(JobListUiState())
    val uiState: StateFlow<JobListUiState> = _uiState.asStateFlow()

    init {
        fetchJobs(isRefresh = true)
        fetchAvailableSkills()
        setupSocketSubscription()
    }

    private fun fetchAvailableSkills() {
        viewModelScope.launch {
            jobRepository.getSkills().onSuccess { skills ->
                _uiState.update { it.copy(availableSkills = skills) }
            }
        }
    }

    private fun setupSocketSubscription() {
        // Lắng nghe thay đổi job từ server để cập nhật ngầm
        disposables.add(socketManager.subscribe("/topic/jobs") {
            refresh(showLoading = false, isSilent = true) 
        })
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun updateLocation(location: String) {
        _uiState.update { it.copy(locationFilter = location, isLocationSheetVisible = false) }
        fetchJobs(isRefresh = true)
    }

    fun updateSalary(min: Double?, max: Double?) {
        _uiState.update { it.copy(salaryMin = min, salaryMax = max, isSalarySheetVisible = false) }
        fetchJobs(isRefresh = true)
    }

    fun updateSkills(skills: String) {
        _uiState.update { it.copy(skillsFilter = skills) }
        fetchJobs(isRefresh = true)
    }

    fun updateLevel(level: String) {
        _uiState.update { it.copy(levelFilter = level) }
        fetchJobs(isRefresh = true)
    }

    fun setCompanyFilter(companyId: String?) {
        _uiState.update { it.copy(companyId = companyId) }
        fetchJobs(isRefresh = true)
    }

    fun toggleLocationSheet(visible: Boolean) { _uiState.update { it.copy(isLocationSheetVisible = visible) } }
    fun toggleSalarySheet(visible: Boolean) { _uiState.update { it.copy(isSalarySheetVisible = visible) } }
    fun toggleOtherSheet(visible: Boolean) { _uiState.update { it.copy(isOtherSheetVisible = visible) } }

    fun applyFilters(
        location: String = _uiState.value.locationFilter,
        skills: String = _uiState.value.skillsFilter,
        salaryMin: Double? = _uiState.value.salaryMin,
        salaryMax: Double? = _uiState.value.salaryMax,
        level: String = _uiState.value.levelFilter
    ) {
        _uiState.update { it.copy(
            locationFilter = location,
            skillsFilter = skills,
            salaryMin = salaryMin,
            salaryMax = salaryMax,
            levelFilter = level,
            isOtherSheetVisible = false
        ) }
        fetchJobs(isRefresh = true)
    }

    fun clearFilters() {
        _uiState.update { it.copy(
            locationFilter = "",
            skillsFilter = "",
            salaryMin = null,
            salaryMax = null,
            levelFilter = "",
            isLocationSheetVisible = false,
            isSalarySheetVisible = false,
            isOtherSheetVisible = false
        ) }
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

    fun refresh(showLoading: Boolean = true, isSilent: Boolean = false) {
        fetchJobs(isRefresh = true, showLoading = showLoading, isSilent = isSilent)
    }

    private fun fetchJobs(isRefresh: Boolean, showLoading: Boolean = true, isSilent: Boolean = false) {
        viewModelScope.launch {
            val currentState = _uiState.value
            
            if (isRefresh) {
                if (showLoading) {
                    _uiState.update { it.copy(isLoading = true, page = 1, error = null) }
                } else {
                    _uiState.update { it.copy(page = 1, error = null) }
                }
            } else {
                _uiState.update { it.copy(isFetchingNextPage = true, error = null) }
            }

            val targetPage = if (isRefresh) 1 else currentState.page + 1
            
            val parts = mutableListOf<String>()
            parts.add("active : true")
            
            if (currentState.searchQuery.isNotBlank()) {
                parts.add("name ~ '%${currentState.searchQuery}%'")
            }
            if (currentState.locationFilter.isNotBlank() && currentState.locationFilter != "ALL") {
                parts.add("location : '${currentState.locationFilter}'")
            }
            if (currentState.skillsFilter.isNotBlank()) {
                val skillNames = currentState.skillsFilter.split(", ").filter { it.isNotBlank() }
                if (skillNames.size == 1) {
                    parts.add("skills.name ~ '%${skillNames[0]}%'")
                } else {
                    val skillQuery = skillNames.joinToString(" or ") { "skills.name ~ '%$it%'" }
                    parts.add("($skillQuery)")
                }
            }
            if (currentState.salaryMin != null) {
                parts.add("salary >= ${currentState.salaryMin.toLong()}")
            }
            if (currentState.salaryMax != null) {
                parts.add("salary <= ${currentState.salaryMax.toLong()}")
            }
            if (currentState.levelFilter.isNotBlank()) {
                parts.add("level : '${currentState.levelFilter}'")
            }
            if (currentState.companyId != null) {
                parts.add("company.id : '${currentState.companyId}'")
            }
            
            val filterString = if (parts.isNotEmpty()) parts.joinToString(" and ") else null

            val result = jobRepository.getJobs(page = targetPage, pageSize = 10, filter = filterString)

            result.onSuccess { paginationData ->
                val newJobs = paginationData.result
                _uiState.update { state ->
                    val mergedJobs = if (isRefresh) {
                        if (isSilent) {
                            // Cập nhật/Thêm mới nhưng giữ nguyên các item cũ để tránh nhảy scroll
                            (newJobs + state.jobs).distinctBy { it.id }
                        } else {
                            // Refresh thủ công: reset hoàn toàn
                            newJobs
                        }
                    } else {
                        // Load more: nối đuôi
                        state.jobs + newJobs
                    }

                    state.copy(
                        isLoading = false,
                        isFetchingNextPage = false,
                        jobs = mergedJobs,
                        page = if (isSilent) state.page else targetPage,
                        totalPages = if (paginationData.meta.pages == 0) 1 else paginationData.meta.pages,
                        hasMoreData = if (isSilent) state.hasMoreData else targetPage < paginationData.meta.pages
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

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
