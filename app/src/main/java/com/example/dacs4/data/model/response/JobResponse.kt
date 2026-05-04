package com.example.dacs4.data.model.response

/**
 * Dữ liệu Công việc trả về từ Spring Boot Backend.
 * Ánh xạ từ interface IJob trong frontend Web (TypeScript).
 */
data class JobResponse(
    val id: String,
    val name: String,
    val skills: List<SkillResponse>? = null,
    val company: CompanyBrief? = null,
    val location: String,
    val salary: Double,
    val quantity: Int,
    val level: String,
    val description: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val isActive: Boolean = true,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class SkillResponse(
    val id: String,
    val name: String
)

/**
 * Thông tin Công ty rút gọn nhúng bên trong Job.
 * (Không phải full CompanyResponse - Backend trả về object lồng nhau)
 */
data class CompanyBrief(
    val id: String?,
    val name: String?,
    val logo: String?
)
