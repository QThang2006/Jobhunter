package com.example.dacs4.ui.screens.application

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs4.data.model.*
import com.example.dacs4.data.repository.ApplicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ═══════════════════════════════════════════════════════════════════
//  APPLICATION VIEW MODEL — Phase 2
//  Quản lý toàn bộ state cho luồng ứng tuyển
//  Một ViewModel dùng chung cho cả 4 màn hình Phase 2
// ═══════════════════════════════════════════════════════════════════

// ──────────────────────────────────────────────────────────────────
//  UI STATES
// ──────────────────────────────────────────────────────────────────

/** State cho form nộp đơn (ApplyJobScreen) */
data class ApplyFormState(
    val currentStep: ApplyStep = ApplyStep.SELECT_CV,
    val selectedCvSource: CvSource = CvSource.None,
    val coverLetter: String = "",
    val coverLetterCharCount: Int = 0,
    val savedCvs: List<SavedCv> = emptyList(),
    val isLoadingSavedCvs: Boolean = false,
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val isSuccess: Boolean = false,
    val submittedApplicationId: String? = null
)

/** State cho danh sách đơn ứng tuyển (MyApplicationsScreen) */
data class ApplicationListState(
    val applications: List<ApplicationResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val filter: ApplicationStatus? = null,         // null = tất cả
    val isRefreshing: Boolean = false
) {
    val filteredApplications: List<ApplicationResponse>
        get() = if (filter == null) applications
                else applications.filter { it.status == filter }
}

/** State cho chi tiết đơn (ApplicationDetailScreen) */
data class ApplicationDetailState(
    val application: ApplicationResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isWithdrawing: Boolean = false,
    val isWithdrawn: Boolean = false
)

// ──────────────────────────────────────────────────────────────────
//  VIEW MODEL
// ──────────────────────────────────────────────────────────────────

@HiltViewModel
class ApplicationViewModel @Inject constructor(
    private val repository: ApplicationRepository
) : ViewModel() {

    // ── State Flows ───────────────────────────────────────────────
    private val _applyForm   = MutableStateFlow(ApplyFormState())
    val applyForm: StateFlow<ApplyFormState> = _applyForm.asStateFlow()

    private val _listState   = MutableStateFlow(ApplicationListState())
    val listState: StateFlow<ApplicationListState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(ApplicationDetailState())
    val detailState: StateFlow<ApplicationDetailState> = _detailState.asStateFlow()

    // ──────────────────────────────────────────────────────────────
    //  APPLY FORM — Actions
    // ──────────────────────────────────────────────────────────────

    /** Khởi tạo form khi mở ApplyJobScreen — load saved CVs */
    fun initApplyForm(jobId: String, jobName: String, companyName: String) {
        _applyForm.update { it.copy(isLoadingSavedCvs = true, submitError = null) }
        viewModelScope.launch {
            repository.getSavedCvs()
                .onSuccess { cvs ->
                    _applyForm.update {
                        it.copy(
                            isLoadingSavedCvs = false,
                            savedCvs = cvs,
                            // Auto-chọn CV mặc định nếu có
                            selectedCvSource = cvs.firstOrNull { cv -> cv.isDefault }
                                ?.let { cv -> CvSource.FromSaved(cv) }
                                ?: CvSource.None
                        )
                    }
                }
                .onFailure {
                    _applyForm.update { state ->
                        state.copy(isLoadingSavedCvs = false)
                    }
                }
        }
    }

    /** Người dùng chọn CV đã lưu */
    fun selectSavedCv(cv: SavedCv) {
        _applyForm.update { it.copy(selectedCvSource = CvSource.FromSaved(cv)) }
    }

    /** Người dùng chọn file từ máy (sau khi file picker trả về URI) */
    fun selectFileFromDevice(uri: String, fileName: String, fileSize: Long) {
        _applyForm.update {
            it.copy(selectedCvSource = CvSource.FromDevice(uri, fileName, fileSize))
        }
    }

    /** Người dùng nhập cover letter */
    fun updateCoverLetter(text: String) {
        _applyForm.update {
            it.copy(
                coverLetter = text,
                coverLetterCharCount = text.length
            )
        }
    }

    /** Chuyển bước trong multi-step form */
    fun goToStep(step: ApplyStep) {
        _applyForm.update { it.copy(currentStep = step) }
    }

    fun nextStep() {
        val current = _applyForm.value.currentStep
        val next = when (current) {
            ApplyStep.SELECT_CV    -> ApplyStep.COVER_LETTER
            ApplyStep.COVER_LETTER -> ApplyStep.CONFIRM
            ApplyStep.CONFIRM      -> ApplyStep.CONFIRM // terminal
        }
        _applyForm.update { it.copy(currentStep = next) }
    }

    fun prevStep() {
        val current = _applyForm.value.currentStep
        val prev = when (current) {
            ApplyStep.SELECT_CV    -> ApplyStep.SELECT_CV // terminal
            ApplyStep.COVER_LETTER -> ApplyStep.SELECT_CV
            ApplyStep.CONFIRM      -> ApplyStep.COVER_LETTER
        }
        _applyForm.update { it.copy(currentStep = prev) }
    }

    /** Gửi đơn ứng tuyển */
    fun submitApplication(jobId: String, jobName: String, companyName: String) {
        val form = _applyForm.value
        val cvSource = form.selectedCvSource
        if (cvSource is CvSource.None) return

        _applyForm.update { it.copy(isSubmitting = true, submitError = null) }

        viewModelScope.launch {
            // Xác định URL CV
            val cvUrl = when (cvSource) {
                is CvSource.FromSaved  -> cvSource.cv.url
                is CvSource.FromDevice -> cvSource.uri // Trong thực tế cần upload trước
                is CvSource.None       -> return@launch
            }

            repository.createApplication(
                cvUrl = cvUrl,
                jobId = jobId,
                jobName = jobName,
                companyName = companyName,
                coverLetter = form.coverLetter
            )
                .onSuccess { application ->
                    _applyForm.update {
                        it.copy(
                            isSubmitting = false,
                            isSuccess = true,
                            submittedApplicationId = application.id
                        )
                    }
                }
                .onFailure { error ->
                    _applyForm.update {
                        it.copy(
                            isSubmitting = false,
                            submitError = error.message ?: "Nộp đơn thất bại. Vui lòng thử lại."
                        )
                    }
                }
        }
    }

    /** Reset form về trạng thái ban đầu */
    fun resetApplyForm() {
        _applyForm.value = ApplyFormState()
    }

    // ──────────────────────────────────────────────────────────────
    //  MY APPLICATIONS LIST — Actions
    // ──────────────────────────────────────────────────────────────

    /** Load danh sách đơn ứng tuyển */
    fun loadMyApplications(isRefresh: Boolean = false) {
        if (_listState.value.isLoading && !isRefresh) return

        _listState.update {
            it.copy(
                isLoading = !isRefresh,
                isRefreshing = isRefresh,
                error = null
            )
        }

        viewModelScope.launch {
            repository.getMyApplications()
                .onSuccess { apps ->
                    _listState.update {
                        it.copy(
                            applications = apps,
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                }
                .onFailure { error ->
                    _listState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = error.message ?: "Không thể tải danh sách"
                        )
                    }
                }
        }
    }

    /** Lọc danh sách theo trạng thái */
    fun setFilter(status: ApplicationStatus?) {
        _listState.update { it.copy(filter = status) }
    }

    // ──────────────────────────────────────────────────────────────
    //  APPLICATION DETAIL — Actions
    // ──────────────────────────────────────────────────────────────

    /** Load chi tiết đơn ứng tuyển */
    fun loadApplicationDetail(id: String) {
        _detailState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            repository.getApplicationDetail(id)
                .onSuccess { app ->
                    _detailState.update { it.copy(application = app, isLoading = false) }
                }
                .onFailure { error ->
                    _detailState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Không thể tải chi tiết"
                        )
                    }
                }
        }
    }

    /** Rút đơn ứng tuyển */
    fun withdrawApplication(id: String, onSuccess: () -> Unit) {
        _detailState.update { it.copy(isWithdrawing = true) }
        viewModelScope.launch {
            repository.deleteApplication(id)
                .onSuccess {
                    _detailState.update { it.copy(isWithdrawing = false, isWithdrawn = true) }
                    // Reload danh sách sau khi rút
                    loadMyApplications(isRefresh = true)
                    onSuccess()
                }
                .onFailure { error ->
                    _detailState.update {
                        it.copy(
                            isWithdrawing = false,
                            error = error.message ?: "Rút đơn thất bại"
                        )
                    }
                }
        }
    }

    /** Reset detail state khi navigate away */
    fun resetDetailState() {
        _detailState.value = ApplicationDetailState()
    }
}
