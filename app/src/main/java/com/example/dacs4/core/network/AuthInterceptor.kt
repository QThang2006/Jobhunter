package com.example.dacs4.core.network

import com.example.dacs4.core.security.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Gác cổng mọi luồng mạng thoát ra khỏi máy.
 * Tự động moi Token JWT từ TokenManager ("Két sắt") để gắn vào Header: "Authorization: Bearer <Token>".
 * Tương đương với đoạn config.headers.Authorization = 'Bearer ...' trong file axios-customize.ts
 */
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    // Danh sách các API công khai không cần gắn Token để tránh rác Header
    private val publicEndpoints = listOf(
        "/api/v1/auth/login",
        "/api/v1/auth/register",
        "/api/v1/auth/forgot-password"
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val path = originalRequest.url.encodedPath

        // Nếu là API công khai -> Cho qua luôn không cần gắn Token
        if (publicEndpoints.any { path.contains(it) }) {
            return chain.proceed(originalRequest)
        }

        // Lấy Token từ kho
        val token = tokenManager.getAccessToken()

        // Nếu có Token -> Klon (Nhân bản) Request cũ và đắp thêm Header Authorization vào
        val newRequestBuilder = originalRequest.newBuilder()
        if (!token.isNullOrEmpty()) {
            newRequestBuilder.addHeader("Authorization", "Bearer $token")
        }
        
        // Đắp thêm Header Accept JSON cho chuẩn API Spring Boot
        newRequestBuilder.addHeader("Accept", "application/json")

        return chain.proceed(newRequestBuilder.build())
    }
}
