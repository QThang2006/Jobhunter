package com.example.dacs4.data.remote

import com.example.dacs4.data.model.UpdateProfileRequest
import com.example.dacs4.data.model.UploadFileResponse
import com.example.dacs4.data.model.UserProfile
import com.example.dacs4.data.model.response.BaseResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

// ═══════════════════════════════════════════════════════════════════
//  PROFILE API SERVICE — Phase 3
//  Endpoints liên quan đến hồ sơ người dùng
// ═══════════════════════════════════════════════════════════════════

interface ProfileApiService {

    /**
     * Lấy thông tin profile của User đang đăng nhập
     * GET /api/v1/users/profile
     * Header: Authorization: Bearer <access_token>  (AuthInterceptor tự gắn)
     */
    @GET("/api/v1/users/profile")
    suspend fun getMyProfile(): Response<BaseResponse<UserProfile>>

    /**
     * Cập nhật profile (partial update)
     * PATCH /api/v1/users
     */
    @PATCH("/api/v1/users")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): Response<BaseResponse<UserProfile>>

    /**
     * Upload ảnh đại diện
     * POST /api/v1/files/upload
     * Dùng chung endpoint upload với CV (phân biệt bằng folder="avatar")
     */
    @Multipart
    @POST("/api/v1/files/upload")
    suspend fun uploadAvatar(
        @Part file: MultipartBody.Part,
        @Part("folder") folder: okhttp3.RequestBody
    ): Response<BaseResponse<UploadFileResponse>>
}
