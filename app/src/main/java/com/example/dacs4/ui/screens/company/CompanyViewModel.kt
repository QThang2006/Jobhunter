package com.example.dacs4.ui.screens.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs4.data.model.Company
import com.example.dacs4.data.model.CompanyScale
import com.example.dacs4.data.repository.CompanyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ═══════════════════════════════════════════════════════════════════
//  COMPANY VIEW MODEL — Phase 3
//  Quản lý state cho CompanyListScreen và CompanyDetailScreen
// ═══════════════════════════════════════════════════════════════════

// ──────────────────────────────────────────────────────────────────
//  UI STATES
// ──────────────────────────────────────────────────────────────────

data class CompanyListState(
    val companies: List<Company> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedIndustry: String = "Tất cả",
    val selectedScale: CompanyScale? = null
) {
    /** Áp thêm filter theo scale (client-side vì API không hỗ trợ) */
    val filtered: List<Company>
        get() = if (selectedScale != null)
            companies.filter { it.scale == selectedScale }
        else companies

    /** Featured companies cho horizontal scroll */
    val featuredCompanies: List<Company>
        get() = companies.filter { it.isFeatured }
}

data class CompanyDetailState(
    val company: Company? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
)

// ──────────────────────────────────────────────────────────────────
//  VIEW MODEL
// ──────────────────────────────────────────────────────────────────

@OptIn(FlowPreview::class)
@HiltViewModel
class CompanyViewModel @Inject constructor(
    private val repository: CompanyRepository
) : ViewModel() {

    private val _listState   = MutableStateFlow(CompanyListState())
    val listState: StateFlow<CompanyListState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(CompanyDetailState())
    val detailState: StateFlow<CompanyDetailState> = _detailState.asStateFlow()

    // Internal debounced search trigger
    private val _rawQuery = MutableStateFlow("")

    init {
        // Debounce search input 400ms — tránh gọi API liên tục khi user đang gõ
        viewModelScope.launch {
            _rawQuery
                .debounce(400)
                .distinctUntilChanged()
                .collect { query ->
                    _listState.update { it.copy(searchQuery = query) }
                    fetchCompanies(
                        query    = query,
                        industry = _listState.value.selectedIndustry
                    )
                }
        }
    }

    // ── List Actions ──────────────────────────────────────────────

    fun loadCompanies(isRefresh: Boolean = false) {
        if (_listState.value.isLoading && !isRefresh) return
        _listState.update {
            it.copy(isLoading = !isRefresh, isRefreshing = isRefresh, error = null)
        }
        viewModelScope.launch {
            fetchCompanies(
                query    = _listState.value.searchQuery,
                industry = _listState.value.selectedIndustry
            )
        }
    }

    /** Kích hoạt search — debounced */
    fun onSearchChange(query: String) {
        _rawQuery.value = query
    }

    /** Chọn ngành nghề filter */
    fun setIndustry(industry: String) {
        _listState.update { it.copy(selectedIndustry = industry) }
        viewModelScope.launch {
            fetchCompanies(query = _listState.value.searchQuery, industry = industry)
        }
    }

    /** Lọc theo quy mô (client-side) */
    fun setScaleFilter(scale: CompanyScale?) {
        _listState.update { it.copy(selectedScale = scale) }
    }

    private suspend fun fetchCompanies(query: String, industry: String) {
        repository.getCompanies(
            query    = query,
            industry = if (industry == "Tất cả") null else industry
        )
            .onSuccess { list ->
                _listState.update {
                    it.copy(companies = list, isLoading = false, isRefreshing = false)
                }
            }
            .onFailure { e ->
                _listState.update {
                    it.copy(
                        isLoading    = false,
                        isRefreshing = false,
                        error        = e.message ?: "Không thể tải danh sách công ty"
                    )
                }
            }
    }

    // ── Detail Actions ────────────────────────────────────────────

    fun loadCompanyDetail(id: String) {
        _detailState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            repository.getCompanyDetail(id)
                .onSuccess { company ->
                    _detailState.update { it.copy(company = company, isLoading = false) }
                }
                .onFailure { e ->
                    _detailState.update {
                        it.copy(
                            isLoading = false,
                            error     = e.message ?: "Không thể tải thông tin công ty"
                        )
                    }
                }
        }
    }

    /** Toggle bookmark/save company */
    fun toggleSave() {
        _detailState.update { it.copy(isSaved = !it.isSaved) }
    }

    /** Reset detail state khi navigate away — tránh flash dữ liệu cũ */
    fun resetDetail() {
        _detailState.value = CompanyDetailState()
    }
}
