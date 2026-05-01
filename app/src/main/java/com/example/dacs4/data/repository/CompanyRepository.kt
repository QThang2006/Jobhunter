package com.example.dacs4.data.repository

import com.example.dacs4.data.model.response.CompanyResponse
import com.example.dacs4.data.model.response.PaginationData
import com.example.dacs4.data.remote.ApiService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tầng trung gian kết nối ViewModel với ApiService cho module Công ty.
 */
@Singleton
class CompanyRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun getCompanies(page: Int = 1, pageSize: Int = 10, filter: String? = null): Result<PaginationData<CompanyResponse>> {
        return try {
            val response = api.getCompanies(page, pageSize, filter = filter)
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

    suspend fun getCompanyById(id: String): Result<CompanyResponse> {
        return try {
            val response = api.getCompanyById(id)
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
}

