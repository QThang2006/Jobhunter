package com.example.dacs4.data.model.response

/**
 * Lớp Wrapper chuẩn để bọc toàn bộ dữ liệu trả về từ Spring Boot Backend.
 * @param T Kiểu dữ liệu linh hoạt (Generic) tương tự interface IBackendRes<T> trên Web.
 */
data class BaseResponse<T>(
    val error: Any? = null, // Có thể là String hoặc List<String>
    val message: String,
    val statusCode: Int,
    val data: T? // Cần null-safe vì nếu lỗi thì không có data
)
