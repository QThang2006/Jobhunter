package com.example.dacs4.data.model.request

data class ChangePasswordRequest(
    val email: String,
    val oldpass: String,
    val newpass: String
)
