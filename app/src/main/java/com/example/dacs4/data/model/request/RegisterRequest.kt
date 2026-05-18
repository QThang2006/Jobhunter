package com.example.dacs4.data.model.request

/**
 * Request đăng ký tài khoản mới.
 * Backend nhận: name, email, password, age, gender, address
 */
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val age: Int,
    val gender: String,      // "MALE" | "FEMALE" | "OTHER"
    val address: String
)
