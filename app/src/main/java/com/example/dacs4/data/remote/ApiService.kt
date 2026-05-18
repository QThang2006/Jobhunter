package com.example.dacs4.data.remote

import com.example.dacs4.data.model.request.ChangePasswordRequest
import com.example.dacs4.data.model.request.CreateResumeRequest
import com.example.dacs4.data.model.request.LoginRequest
import com.example.dacs4.data.model.request.RegisterRequest
import com.example.dacs4.data.model.request.UpdateUserRequest
import com.example.dacs4.data.model.response.AuthResponse
import com.example.dacs4.data.model.response.BaseResponse
import com.example.dacs4.data.model.response.CompanyResponse
import com.example.dacs4.data.model.response.JobResponse
import com.example.dacs4.data.model.response.PaginationData
import com.example.dacs4.data.model.response.ResumeResponse
import com.example.dacs4.data.model.response.UploadFileResponse
import com.example.dacs4.data.model.response.UserDetailResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Trung tâm chỉ huy gọi Server - Ánh xạ api.ts trên Web Frontend.
 * Toàn bộ các API đều dùng định dạng trả về là BaseResponse<T>.
 */
interface ApiService {

    // ------------------- AUTH MODULE -------------------
    @POST("/api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<BaseResponse<AuthResponse>>

    @POST("/api/v1/auth/logout")
    suspend fun logout(): Response<BaseResponse<String>>

    // Dành cho TokenAuthenticator (Dùng `Call` thay suspend để chạy đồng bộ)
    @GET("/api/v1/auth/refresh")
    fun refreshTokenSync(): Call<BaseResponse<AuthResponse>>

    @POST("/api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<BaseResponse<Any>>

    @POST("/api/v1/auth/forgot-password")
    suspend fun forgotPassword(@Body body: Map<String, String>): Response<BaseResponse<Any>>

    @POST("/api/v1/auth/verify-otp")
    suspend fun verifyOtp(@Body body: Map<String, String>): Response<Boolean>

    @POST("/api/v1/auth/reset-password")
    suspend fun resetPassword(@Body body: Map<String, String>): Response<BaseResponse<Any>>

    // ------------------- JOB MODULE -------------------
    @GET("/api/v1/jobs")
    suspend fun getJobs(
        @Query("current") page: Int = 1,
        @Query("pageSize") pageSize: Int = 10,
        @Query("sort") sort: String = "createdAt,desc",
        @Query("filter") filter: String? = null
    ): Response<BaseResponse<PaginationData<JobResponse>>>

    @GET("/api/v1/jobs/{id}")
    suspend fun getJobById(
        @Path("id") id: String
    ): Response<BaseResponse<JobResponse>>

    // ------------------- COMPANY MODULE -------------------
    @GET("/api/v1/companies")
    suspend fun getCompanies(
        @Query("current") page: Int = 1,
        @Query("pageSize") pageSize: Int = 10,
        @Query("filter") filter: String? = null
    ): Response<BaseResponse<PaginationData<CompanyResponse>>>

    @GET("/api/v1/companies/{id}")
    suspend fun getCompanyById(
        @Path("id") id: String
    ): Response<BaseResponse<CompanyResponse>>

    // ------------------- FILE MODULE -------------------
    @Multipart
    @POST("/api/v1/files")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part,
        @Part("folder") folder: RequestBody
    ): Response<BaseResponse<UploadFileResponse>>

    // ------------------- RESUME MODULE -------------------
    @POST("/api/v1/resumes")
    suspend fun createResume(
        @Body request: CreateResumeRequest
    ): Response<BaseResponse<ResumeResponse>>

    @POST("/api/v1/resumes/by-user")
    suspend fun getMyResumes(
        @Query("current") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Response<BaseResponse<PaginationData<ResumeResponse>>>

    // ------------------- USER MODULE -------------------
    @GET("/api/v1/users/{id}")
    suspend fun getUserById(
        @Path("id") id: Long
    ): Response<BaseResponse<UserDetailResponse>>

    @PUT("/api/v1/users")
    suspend fun updateUser(
        @Body request: UpdateUserRequest
    ): Response<BaseResponse<UserDetailResponse>>

    @POST("/api/v1/users/change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): Response<BaseResponse<String>>

    // ------------------- SKILL MODULE -------------------
    @GET("/api/v1/skills")
    suspend fun getSkills(
        @Query("current") page: Int = 1,
        @Query("pageSize") pageSize: Int = 100
    ): Response<BaseResponse<PaginationData<com.example.dacs4.data.model.response.SkillResponse>>>
}
