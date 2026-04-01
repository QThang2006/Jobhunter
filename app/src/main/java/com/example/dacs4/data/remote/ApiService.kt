package com.example.dacs4.data.remote

import com.example.dacs4.data.model.request.LoginRequest
import com.example.dacs4.data.model.response.AuthResponse
import com.example.dacs4.data.model.response.BaseResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Trung tâm chỉ huy gọi Server. Ánh xạ trực tiếp file trúc `api.ts` trên Web.
 * Toàn bộ các API đều dùng định dạng trả về là BaseResponse<T>.
 */
interface ApiService {

    // ------------------- AUTH MODULE -------------------
    @POST("/api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<BaseResponse<AuthResponse>>

    @POST("/api/v1/auth/logout")
    suspend fun logout(): Response<BaseResponse<String>>

    // Dành cho Mutex Authenticator (Nổi bật: Dùng `Call` thay vì `suspend` để bắt buộc chạy Đồng bộ)
    // Lưu ý: Spring Boot sẽ nhận diện Refresh Token qua HTTP-Only Cookies do OkHttp tự dính theo request
    @GET("/api/v1/auth/refresh")
    fun refreshTokenSync(): Call<BaseResponse<AuthResponse>>
    
    // ------------------- JOB MODULE (Sẽ Cập nhật ở Milestone sau) -------------------
    
    // ------------------- RESUME MODULE (Sẽ Cập nhật ở Milestone sau) -------------------
}
