package com.example.dacs4.data.remote

import com.example.dacs4.data.model.request.LoginRequest
import com.example.dacs4.data.model.response.AuthResponse
import com.example.dacs4.data.model.response.BaseResponse
import com.example.dacs4.data.model.response.CompanyResponse
import com.example.dacs4.data.model.response.JobResponse
import com.example.dacs4.data.model.response.PaginationData
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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

    // ------------------- JOB MODULE -------------------
    @GET("/api/v1/jobs")
    suspend fun getJobs(
        @Query("current") page: Int = 1,
        @Query("pageSize") pageSize: Int = 10,
        @Query("sort") sort: String = "createdAt,desc"
    ): Response<BaseResponse<PaginationData<JobResponse>>>

    @GET("/api/v1/jobs/{id}")
    suspend fun getJobById(
        @Path("id") id: String
    ): Response<BaseResponse<JobResponse>>

    // ------------------- COMPANY MODULE -------------------
    @GET("/api/v1/companies")
    suspend fun getCompanies(
        @Query("current") page: Int = 1,
        @Query("pageSize") pageSize: Int = 10
    ): Response<BaseResponse<PaginationData<CompanyResponse>>>

    @GET("/api/v1/companies/{id}")
    suspend fun getCompanyById(
        @Path("id") id: String
    ): Response<BaseResponse<CompanyResponse>>
}
