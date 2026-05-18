package com.example.dacs4.data.model.request

data class UpdateUserRequest(
    val id: Long,
    val name: String,
    val age: Int,
    val gender: String,
    val address: String,
    val email: String
)
