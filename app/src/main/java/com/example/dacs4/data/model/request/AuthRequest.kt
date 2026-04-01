package com.example.dacs4.data.model.request

/**
 * Body Request khi gửi yêu cầu Login 
 * Tương đương với object body của `callLogin` trên Web.
 */
data class LoginRequest(
    val username: String, // Trong thực tế là nhập trường Email
    val password: String
)

/**
 * Body Request khi điền Form Quên Mật Khẩu
 */
data class ForgotPasswordRequest(
    val email: String
)
