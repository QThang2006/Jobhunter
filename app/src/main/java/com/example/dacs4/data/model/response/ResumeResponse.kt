package com.example.dacs4.data.model.response

data class ResumeResponse(
    val id: Long,
    val email: String,
    val url: String,
    val status: String,        // "PENDING" | "REVIEWING" | "APPROVED" | "REJECTED"
    val createdAt: String?,
    val updatedAt: String?,
    val companyName: String?,
    val user: ResumeUserInfo?,
    val job: ResumeJobInfo?
)

data class ResumeUserInfo(val id: Long, val name: String)
data class ResumeJobInfo(val id: Long, val name: String)
