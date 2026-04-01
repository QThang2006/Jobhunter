package com.example.dacs4.data.model.response

/**
 * Phản hồi khi gọi API Login Thành Công
 * Giống interface IAccount trên file backend.d.ts
 */
data class AuthResponse(
    val access_token: String,
    val user: UserInfo
)

/**
 * Phản hồi thông tin User cơ bản 
 * Tương đương interface IUser trong file backend.d.ts
 */
data class UserInfo(
    val id: String,
    val email: String,
    val name: String,
    val role: RoleInfo? = null, // Backend có thể không trả về tuỳ Endpoint
    val company: CompanyInfo? = null
)

data class RoleInfo(
    val id: String,
    val name: String
)

data class CompanyInfo(
    val id: String,
    val name: String
)
