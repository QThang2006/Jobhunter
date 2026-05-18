package com.example.dacs4.data.model.request

data class CreateResumeRequest(
    val status: String = "PENDING",
    val email: String,
    val url: String,
    val user: IdWrapper,
    val job: IdWrapper
)

data class IdWrapper(val id: Long)
