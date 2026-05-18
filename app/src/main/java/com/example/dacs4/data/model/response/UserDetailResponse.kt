package com.example.dacs4.data.model.response

data class UserDetailResponse(
    val id: Long,
    val name: String,
    val email: String,
    val age: Int?,
    val gender: String?,     // "MALE" | "FEMALE" | "OTHER"
    val address: String?,
    val company: CompanyInfo?,
    val role: RoleInfo?
)
