package com.example.dacs4.ui.screens.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs4.data.model.response.CompanyResponse
import com.example.dacs4.data.repository.CompanyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.dacs4.core.socket.SocketManager
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

data class CompanyListUiState(
    val isLoading: Boolean = false,
    val isFetchingNextPage: Boolean = false,
    val companies: List<CompanyResponse> = emptyList(),
    val error: String? = null,
    val page: Int = 1,
    val totalPages: Int = 1,
    val hasMoreData: Boolean = true,
    val searchQuery: String = ""
)

@HiltViewModel
class CompanyListViewModel @Inject constructor(
    private val companyRepository: CompanyRepository,
    private val socketManager: SocketManager
) : ViewModel() {
    
    private val disposables = CompositeDisposable()

    private val _uiState = MutableStateFlow(CompanyListUiState())
    val uiState: StateFlow<CompanyListUiState> = _uiState.asStateFlow()

    init {
        fetchCompanies(isRefresh = true)
        setupSocketSubscription()
    }

    private fun setupSocketSubscription() {
        disposables.add(socketManager.subscribe("/topic/companies") {
            refresh(showLoading = false, isSilent = true)
        })
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun performSearch() {
        fetchCompanies(isRefresh = true)
    }

    fun fetchNextPage() {
        val state = _uiState.value
        if (state.hasMoreData && !state.isLoading && !state.isFetchingNextPage) {
            fetchCompanies(isRefresh = false)
        }
    }

    fun refresh(showLoading: Boolean = true, isSilent: Boolean = false) {
        fetchCompanies(isRefresh = true, showLoading = showLoading, isSilent = isSilent)
    }

    private fun fetchCompanies(isRefresh: Boolean, showLoading: Boolean = true, isSilent: Boolean = false) {
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

            val filterString = if (currentState.searchQuery.isNotBlank()) "name~'%${currentState.searchQuery}%'" else null

            val result = companyRepository.getCompanies(page = targetPage, pageSize = 10, filter = filterString)

            result.onSuccess { paginationData ->
                val newCompanies = paginationData.result
                _uiState.update { state ->
                    val mergedCompanies = if (isRefresh) {
                        if (isSilent) {
                            (newCompanies + state.companies).distinctBy { it.id }
                        } else {
                            newCompanies
                        }
                    } else {
                        state.companies + newCompanies
                    }

                    state.copy(
                        isLoading = false,
                        isFetchingNextPage = false,
                        companies = mergedCompanies,
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
                        error = e.message ?: "Đã xảy ra lỗi lấy dữ liệu công ty"
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
