package com.example.dacs4.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs4.data.model.response.CompanyResponse
import com.example.dacs4.data.model.response.JobResponse
import com.example.dacs4.data.repository.AuthRepository
import com.example.dacs4.data.repository.CompanyRepository
import com.example.dacs4.data.repository.JobRepository
import com.example.dacs4.data.model.response.SkillResponse
import com.example.dacs4.core.security.TokenManager
import com.example.dacs4.core.socket.SocketManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val jobs: List<JobResponse> = emptyList(),
    val companies: List<CompanyResponse> = emptyList(),
    val skills: List<SkillResponse> = emptyList(),
    val selectedSkill: String? = null,
    val error: String? = null
)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val companyRepository: CompanyRepository,
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager,
    private val socketManager: SocketManager
) : ViewModel() {

    private val disposables = CompositeDisposable()

    val userName: String = tokenManager.getUserName() ?: "Bạn"

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        fetchHomeData()
        setupSocketSubscriptions()
    }

    private fun setupSocketSubscriptions() {
        // Lắng nghe thay đổi từ server để tự động cập nhật UI ngầm (background)
        disposables.add(socketManager.subscribe("/topic/jobs") {
            fetchHomeData(showLoading = false) 
        })

        disposables.add(socketManager.subscribe("/topic/companies") {
            fetchHomeData(showLoading = false)
        })
    }

    fun fetchHomeData(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) {
                _uiState.update { it.copy(isLoading = true, error = null) }
            }
            
            // Gọi song song 3 API (async) để tối ưu thời gian chờ
            val jobsDeferred = async { 
                val filter = if (_uiState.value.selectedSkill != null) {
                    "active : true and skills.name ~ '%${_uiState.value.selectedSkill}%'"
                } else {
                    "active : true"
                }
                jobRepository.getJobs(1, 10, filter = filter) 
            }
            val companiesDeferred = async { companyRepository.getCompanies(1, 10) }
            val skillsDeferred = async { jobRepository.getSkills() }
 
            val jobsResult = jobsDeferred.await()
            val companiesResult = companiesDeferred.await()
            val skillsResult = skillsDeferred.await()
 
            if (jobsResult.isSuccess && companiesResult.isSuccess && skillsResult.isSuccess) {
                val jobs = jobsResult.getOrNull()?.result ?: emptyList()
                val companies = companiesResult.getOrNull()?.result ?: emptyList()
                val allSkills = skillsResult.getOrNull() ?: emptyList()
                
                // Keep the order from API and take first 20
                val displaySkills = allSkills.take(20)
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        jobs = jobs,
                        companies = companies,
                        skills = displaySkills,
                        error = null
                    )
                }
            } else {
                val errorMsg = jobsResult.exceptionOrNull()?.message
                    ?: companiesResult.exceptionOrNull()?.message
                    ?: skillsResult.exceptionOrNull()?.message
                    ?: "Lỗi không xác định"
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = errorMsg
                    )
                }
            }
        }
    }

    fun onSkillSelected(skillName: String?) {
        if (_uiState.value.selectedSkill == skillName) {
            _uiState.update { it.copy(selectedSkill = null) }
        } else {
            _uiState.update { it.copy(selectedSkill = skillName) }
        }
        fetchHomeData(showLoading = true)
    }

    fun logout() {
        // Gọi API logout (thông báo server) và xoá token local
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear() // Hủy đăng ký khi ViewModel bị tiêu hủy
    }
}
