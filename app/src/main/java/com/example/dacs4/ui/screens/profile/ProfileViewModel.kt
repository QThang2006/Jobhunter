package com.example.dacs4.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs4.data.model.Gender
import com.example.dacs4.data.model.UpdateProfileRequest
import com.example.dacs4.data.model.UserProfile
import com.example.dacs4.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ═══════════════════════════════════════════════════════════════════
//  PROFILE VIEW MODEL — Phase 3
//  Quản lý state cho ProfileScreen và EditProfileScreen
// ═══════════════════════════════════════════════════════════════════

// ──────────────────────────────────────────────────────────────────
//  UI STATES
// ──────────────────────────────────────────────────────────────────

data class ProfileUiState(
    val profile: UserProfile? = null,
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val isUploadingAvatar: Boolean = false,
    val error: String? = null,
    val updateSuccess: Boolean = false
)

/**
 * Trạng thái của form chỉnh sửa — mirror các field của UserProfile
 * Tách riêng để không làm bẩn ProfileUiState khi user đang nhập
 */
data class EditFormState(
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val age: String = "",
    val gender: Gender = Gender.MALE,
    val bio: String = "",
    val skillsRaw: String = "",         // CSV: "Kotlin, MVVM, Hilt"
    val linkedIn: String = "",
    val github: String = "",
    val website: String = "",
    val yearsOfExperience: String = "",
    val desiredPosition: String = "",
    val desiredSalary: String = "",
    val isLookingForJob: Boolean = true
) {
    /** Chuyển form state → request body để gửi lên API */
    fun toRequest() = UpdateProfileRequest(
        name               = name,
        phone              = phone,
        address            = address,
        age                = age.toIntOrNull(),
        gender             = gender.name,
        bio                = bio,
        skills             = skillsRaw.split(",").map { it.trim() }.filter { it.isNotEmpty() },
        linkedIn           = linkedIn,
        github             = github,
        website            = website,
        yearsOfExperience  = yearsOfExperience.toIntOrNull() ?: 0,
        desiredPosition    = desiredPosition,
        desiredSalary      = desiredSalary.toLongOrNull(),
        isLookingForJob    = isLookingForJob
    )

    /** Validation: chỉ cần name hợp lệ */
    val isValid: Boolean get() = name.isNotBlank() && name.length >= 2
}

// ──────────────────────────────────────────────────────────────────
//  VIEW MODEL
// ──────────────────────────────────────────────────────────────────

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _uiState  = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _editForm = MutableStateFlow(EditFormState())
    val editForm: StateFlow<EditFormState> = _editForm.asStateFlow()

    // ── Load ──────────────────────────────────────────────────────

    fun loadProfile() {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            repository.getMyProfile()
                .onSuccess { profile ->
                    _uiState.update { it.copy(profile = profile, isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    // ── Edit Form ─────────────────────────────────────────────────

    /** Khởi tạo form với dữ liệu từ profile hiện tại */
    fun initEditForm() {
        val p = _uiState.value.profile ?: return
        _editForm.value = EditFormState(
            name              = p.name,
            phone             = p.phone,
            address           = p.address,
            age               = p.age?.toString() ?: "",
            gender            = p.gender,
            bio               = p.bio,
            skillsRaw         = p.skills.joinToString(", "),
            linkedIn          = p.linkedIn,
            github            = p.github,
            website           = p.website,
            yearsOfExperience = p.yearsOfExperience.toString(),
            desiredPosition   = p.desiredPosition,
            desiredSalary     = p.desiredSalary?.toString() ?: "",
            isLookingForJob   = p.isLookingForJob
        )
    }

    /** Generic field updater — dùng lambda để giữ DSL gọn */
    fun updateField(update: EditFormState.() -> EditFormState) {
        _editForm.update { it.update() }
    }

    // ── Save ──────────────────────────────────────────────────────

    fun saveProfile() {
        val form = _editForm.value
        if (!form.isValid) return
        _uiState.update { it.copy(isUpdating = true, error = null, updateSuccess = false) }
        viewModelScope.launch {
            repository.updateProfile(form.toRequest())
                .onSuccess { updated ->
                    _uiState.update {
                        it.copy(profile = updated, isUpdating = false, updateSuccess = true)
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isUpdating = false, error = e.message) }
                }
        }
    }

    // ── Avatar ────────────────────────────────────────────────────

    fun uploadAvatar(localUri: String) {
        _uiState.update { it.copy(isUploadingAvatar = true) }
        viewModelScope.launch {
            repository.uploadAvatar(localUri)
                .onSuccess { avatarUrl ->
                    // Update profile với avatar URL mới
                    val updated = _uiState.value.profile?.copy(avatar = avatarUrl)
                    _uiState.update { it.copy(profile = updated, isUploadingAvatar = false) }
                }
                .onFailure {
                    _uiState.update { it.copy(isUploadingAvatar = false) }
                }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────

    fun resetUpdateSuccess() {
        _uiState.update { it.copy(updateSuccess = false) }
    }
}
