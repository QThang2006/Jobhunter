package com.example.dacs4.data.model.response

/**
 * Cấu trúc dữ liệu phân trang từ Spring Boot Backend.
 * Tương đương interface IModelPaginate<T> trên Web Frontend.
 */
data class PaginationData<T>(
    val meta: PaginationMeta,
    val result: List<T>
)

data class PaginationMeta(
    val current: Int,
    val pageSize: Int,
    val pages: Int,
    val total: Long
)
