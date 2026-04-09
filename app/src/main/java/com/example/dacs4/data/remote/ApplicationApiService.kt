package com.example.dacs4.data.remote

import com.example.dacs4.data.model.ApplicationListResponse
import com.example.dacs4.data.model.ApplicationResponse
import com.example.dacs4.data.model.CreateApplicationRequest
import com.example.dacs4.data.model.UploadFileResponse
import com.example.dacs4.data.model.response.BaseResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

// ═══════════════════════════════════════════════════════════════════
//  APPLICATION API SERVICE — Phase 2
//  Định nghĩa tất cả endpoint liên quan đến luồng ứng tuyển
// ═══════════════════════════════════════════════════════════════════

interface ApplicationApiService {

    // ──────────────────────────────────────────────────────────────
    //  FILE UPLOAD
    // ──────────────────────────────────────────────────────────────

    /**
     * Upload file CV lên server
     * Backend nhận multipart/form-data, trả về tên file đã lưu
     *
     * Usage:
     *   val part = MultipartBody.Part.createFormData("fileUpload", fileName, requestBody)
     *   val response = api.uploadFile(part, "resume")
     */
    @Multipart
    @POST("/api/v1/files/upload")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part,
        @Part("folder") folder: okhttp3.RequestBody
    ): Response<BaseResponse<UploadFileResponse>>

    // ──────────────────────────────────────────────────────────────
    //  APPLICATIONS (RESUMES)
    //  NOTE: Backend gọi là "resumes" nhưng thực chất là "application"
    //  (CV đính kèm đơn ứng tuyển, không phải resume profile)
    // ──────────────────────────────────────────────────────────────

    /**
     * Nộp đơn ứng tuyển mới
     * POST /api/v1/resumes
     */
    @POST("/api/v1/resumes")
    suspend fun createApplication(
        @Body request: CreateApplicationRequest
    ): Response<BaseResponse<ApplicationResponse>>

    /**
     * Lấy danh sách đơn ứng tuyển của User hiện tại
     * GET /api/v1/resumes/by-user
     *
     * @param current   Trang hiện tại (bắt đầu từ 1)
     * @param pageSize  Số item mỗi trang
     */
    @GET("/api/v1/resumes/by-user")
    suspend fun getMyApplications(
        @Query("current") current: Int = 1,
        @Query("pageSize") pageSize: Int = 10
    ): Response<BaseResponse<ApplicationListResponse>>

    /**
     * Lấy chi tiết một đơn ứng tuyển theo ID
     * GET /api/v1/resumes/{id}
     */
    @GET("/api/v1/resumes/{id}")
    suspend fun getApplicationDetail(
        @Path("id") id: String
    ): Response<BaseResponse<ApplicationResponse>>

    /**
     * Xoá đơn ứng tuyển (rút đơn)
     * DELETE /api/v1/resumes/{id}
     */
    @DELETE("/api/v1/resumes/{id}")
    suspend fun deleteApplication(
        @Path("id") id: String
    ): Response<BaseResponse<String>>
}
