package com.example.dacs4.data.repository

import com.example.dacs4.data.model.Gender
import com.example.dacs4.data.model.UpdateProfileRequest
import com.example.dacs4.data.model.UserProfile
import com.example.dacs4.data.model.mockUserProfile
import com.example.dacs4.data.remote.ProfileApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

// ═══════════════════════════════════════════════════════════════════
//  PROFILE REPOSITORY — Phase 3
//  Single source of truth cho dữ liệu Profile
// ═══════════════════════════════════════════════════════════════════

@Singleton
class ProfileRepository @Inject constructor(
    private val apiService: ProfileApiService
) {

    // ──────────────────────────────────────────────────────────────
    //  GET PROFILE
    // ──────────────────────────────────────────────────────────────

    suspend fun getMyProfile(): Result<UserProfile> = withContext(Dispatchers.IO) {
        runCatching {
            delay(600)
            mockUserProfile  // Mock — xoá khi có API thật

            /* REAL API:
            val response = apiService.getMyProfile()
            if (response.isSuccessful && response.body()?.data != null) {
                response.body()!!.data!!
            } else {
                throw Exception(response.body()?.message ?: "Không thể tải hồ sơ")
            }
            */
        }
    }

    // ──────────────────────────────────────────────────────────────
    //  UPDATE PROFILE
    // ──────────────────────────────────────────────────────────────

    suspend fun updateProfile(request: UpdateProfileRequest): Result<UserProfile> =
        withContext(Dispatchers.IO) {
            runCatching {
                delay(1000)
                // Mock: echo lại request với các field đã update
                mockUserProfile.copy(
                    name                = request.name,
                    phone               = request.phone,
                    address             = request.address,
                    age                 = request.age,
                    gender              = Gender.valueOf(request.gender),
                    bio                 = request.bio,
                    skills              = request.skills,
                    linkedIn            = request.linkedIn,
                    github              = request.github,
                    website             = request.website,
                    yearsOfExperience   = request.yearsOfExperience,
                    desiredPosition     = request.desiredPosition,
                    desiredSalary       = request.desiredSalary,
                    isLookingForJob     = request.isLookingForJob
                )

                /* REAL API:
                val response = apiService.updateProfile(request)
                if (response.isSuccessful && response.body()?.data != null) {
                    response.body()!!.data!!
                } else {
                    throw Exception(response.body()?.message ?: "Cập nhật thất bại")
                }
                */
            }
        }

    // ──────────────────────────────────────────────────────────────
    //  UPLOAD AVATAR
    // ──────────────────────────────────────────────────────────────

    /**
     * Upload ảnh đại diện và trả về URL đầy đủ
     * @param localUri  URI của ảnh trên thiết bị (từ ImagePicker)
     * @return URL ảnh trên server để lưu vào profile
     */
    suspend fun uploadAvatar(localUri: String): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                delay(1200)
                // Mock: trả về URL giả
                "https://api.jobhunter.vn/images/avatar/mock_${System.currentTimeMillis()}.jpg"

                /* REAL API:
                val file = File(localUri)
                val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("fileUpload", file.name, requestBody)
                val folder = "avatar".toRequestBody("text/plain".toMediaTypeOrNull())

                val response = apiService.uploadAvatar(part, folder)
                if (response.isSuccessful && response.body()?.data != null) {
                    val fileName = response.body()!!.data!!.fileName
                    "https://api.jobhunter.vn/images/avatar/$fileName"
                } else {
                    throw Exception(response.body()?.message ?: "Upload thất bại")
                }
                */
            }
        }
}
