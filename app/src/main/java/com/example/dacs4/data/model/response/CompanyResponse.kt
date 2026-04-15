package com.example.dacs4.data.model.response

/**
 * Dữ liệu Công ty trả về từ Spring Boot Backend.
 * Ánh xạ từ interface ICompany trong frontend Web.
 */
data class CompanyResponse(
    val id: String,
    val name: String,
    val description: String? = null,
    val address: String? = null,
    val logo: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
