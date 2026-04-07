package com.example.dacs4.data.repository

import com.example.dacs4.data.model.response.JobResponse
import com.example.dacs4.data.model.response.PaginationData
import com.example.dacs4.data.remote.ApiService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tầng trung gian kết nối ViewModel với ApiService cho module Việc làm.
 * Bọc kết quả trong Result<T> để ngăn chặn Crash khi gặp lỗi mạng.
 */
@Singleton
class JobRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun getJobs(page: Int = 1, pageSize: Int = 10): Result<PaginationData<JobResponse>> {
        return try {
            val response = api.getJobs(page, pageSize)
            if (response.isSuccessful) {
                val data = response.body()?.data
                if (data != null) Result.success(data)
                else Result.failure(Exception("Không có dữ liệu trả về"))
            } else {
                Result.failure(Exception("Lỗi server: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getJobById(id: String): Result<JobResponse> {
        return try {
            val response = api.getJobById(id)
            if (response.isSuccessful) {
                val data = response.body()?.data
                if (data != null) Result.success(data)
                else Result.failure(Exception("Không tìm thấy công việc"))
            } else {
                Result.failure(Exception("Lỗi server: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
